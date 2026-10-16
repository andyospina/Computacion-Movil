package com.example.myapplication

import com.example.myapplication.data.dto.ProductDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreProductData
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.FirestoreUserDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UserProfileDto
import com.example.myapplication.data.dto.toFirestoreDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Los JSON de ejemplo son respuestas reales del backend Express (Sprint 8). */
class DtoMappingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun product_mapsIdsAndComputesReviewStats() {
        val dto = json.decodeFromString<ProductDto>(
            """{"id":1,"name":"Bamboo Ball","category":"Libros","rating":1.2,
            "createdAt":"2026-10-08T22:50:15.571Z","updatedAt":"2026-10-08T22:50:15.571Z",
            "reviews":[
              {"id":1,"rating":5,"title":"a","body":"b","recommended":true,"userId":6,"productId":1,"createdAt":"2026-10-08T22:50:15.579Z"},
              {"id":2,"rating":5,"title":"a","body":"b","recommended":true,"userId":4,"productId":1},
              {"id":3,"rating":1,"title":"a","body":"b","recommended":false,"userId":4,"productId":1},
              {"id":4,"rating":3,"title":"a","body":"b","recommended":true,"userId":4,"productId":1}]}"""
        )

        val product = dto.toProduct()

        assertEquals("1", product.id)
        assertEquals(4, product.reviewCount)
        assertEquals(75, product.recommendPercent)
        assertEquals(5 to 0.5f, product.ratingBreakdown.first())
        assertEquals(listOf(5, 4, 3, 2, 1), product.ratingBreakdown.map { it.first })
    }

    @Test
    fun product_withoutReviewsHasZeroStats() {
        val product = json.decodeFromString<ProductDto>(
            """{"id":2,"name":"X","category":"Hogar","rating":3.0}"""
        ).toProduct()

        assertEquals(0, product.reviewCount)
        assertEquals(0, product.recommendPercent)
        assertTrue(product.ratingBreakdown.all { it.second == 0f })
    }

    @Test
    fun review_mapsAuthorProductAndStringIds() {
        val review = json.decodeFromString<ReviewDto>(
            """{"id":1,"rating":2,"title":"T","body":"B","recommended":true,"userId":6,"productId":1,
            "createdAt":"2026-10-08T22:50:15.579Z","updatedAt":"2026-10-08T22:50:15.579Z",
            "user":{"id":6,"username":"user6","name":"Kendall Schumm"},
            "product":{"id":1,"name":"Intelligent Bamboo Ball"}}"""
        ).toReview()

        assertEquals("1", review.id)
        assertEquals("1", review.productId)
        assertEquals("6", review.userId)
        assertEquals("Kendall Schumm", review.author)
        assertEquals("Intelligent Bamboo Ball", review.productName)
        assertTrue(review.date.contains("2026"))
    }

    @Test
    fun user_mapsInitialsAndMemberSince() {
        val user = json.decodeFromString<UserProfileDto>(
            """{"id":1,"username":"user1","email":"user1@example.com","name":"Guadalupe Barrows-Herman",
            "profileImage":"https://x/y","website":null,"bio":null,"followersCount":0,
            "createdAt":"2026-10-08T22:50:15.527Z"}"""
        ).toUser()

        assertEquals("Guadalupe Barrows-Herman", user.name)
        assertEquals("GB", user.initials)
        assertEquals(2026, user.memberSinceYear)
    }

    @Test
    fun firestoreUser_mapsAllFields() {
        val user = FirestoreUserDto(
            id = "uid123", username = "ada", name = "Ada Lovelace", email = "a@x.com",
            country = "UK", bio = "Matemática", profileImage = "https://x/y"
        ).toUser()

        assertEquals("Ada Lovelace", user.name)
        assertEquals("AL", user.initials)
        assertEquals("ada", user.username)
        assertEquals("UK", user.country)
        assertEquals("Matemática", user.bio)
        assertEquals("https://x/y", user.profileImage)
    }

    @Test
    fun firestoreUser_withoutOptionalFields_usesDefaults() {
        val user = FirestoreUserDto(username = "bob", name = "Bob").toUser()

        assertEquals("", user.bio)
        assertEquals("", user.country)
        assertEquals(0, user.memberSinceYear)
        assertEquals(null, user.profileImage)
    }

    @Test
    fun registerUser_isTrimmedForFirestore() {
        val dto = RegisterUserDto(
            username = " ada ", name = " Ada ", email = " a@x.com ", country = "", bio = ""
        ).toFirestoreDto()

        assertEquals("ada", dto.username)
        assertEquals("Ada", dto.name)
        assertEquals("a@x.com", dto.email)
        assertEquals("", dto.country)
    }

    @Test
    fun firestoreReview_mapsDenormalizedAuthorAndKeepsDocumentId() {
        val review = FirestoreReviewDto(
            id = "abc", userId = "uid1", productId = "p1", productName = "Audífonos", rating = 4,
            title = "t", body = "b", user = FirestoreAuthorDto(name = "Ada", username = "ada")
        ).toReview()

        assertEquals("abc", review.id)
        assertEquals("uid1", review.userId)
        assertEquals("p1", review.productId)
        assertEquals("Ada", review.author)
        assertEquals("Audífonos", review.productName)
        assertEquals("", review.date)
    }

    @Test
    fun firestoreReview_authorFallsBackToUsername() {
        val review = FirestoreReviewDto(user = FirestoreAuthorDto(username = "ada")).toReview()
        assertEquals("ada", review.author)
    }

    @Test
    fun firestoreProduct_computesStatsFromItsReviews() {
        val product = FirestoreProductData(
            FirestoreProductDto(id = "p1", name = "X", category = "Hogar"),
            listOf(
                FirestoreReviewDto(rating = 5, recommended = true),
                FirestoreReviewDto(rating = 4, recommended = true),
                FirestoreReviewDto(rating = 1, recommended = false),
                FirestoreReviewDto(rating = 2, recommended = false)
            )
        ).toProduct()

        assertEquals("p1", product.id)
        assertEquals(3.0, product.rating, 0.0)
        assertEquals(4, product.reviewCount)
        assertEquals(50, product.recommendPercent)
        assertEquals(5 to 0.25f, product.ratingBreakdown.first())
    }

    @Test
    fun firestoreProduct_withoutReviewsHasZeroStats() {
        val product = FirestoreProductData(FirestoreProductDto(id = "p2", name = "Y"), emptyList()).toProduct()
        assertEquals(0.0, product.rating, 0.0)
        assertEquals(0, product.reviewCount)
    }

    @Test
    fun newReview_copiesAuthorIntoFirestoreDocument() {
        val dto = NewReviewDto(
            productId = "p1", productName = "X", userId = "uid1", authorName = "Ada",
            authorUsername = "ada", authorImage = null, rating = 5, title = "t", body = "b",
            recommended = true
        ).toFirestoreDto()

        assertEquals("uid1", dto.userId)
        assertEquals("Ada", dto.user.name)
        assertEquals("ada", dto.user.username)
        assertEquals("X", dto.productName)
    }
}

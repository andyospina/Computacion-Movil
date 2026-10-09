package com.example.myapplication

import com.example.myapplication.data.dto.ProductDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UserProfileDto
import com.example.myapplication.data.dto.toProduct
import com.example.myapplication.data.dto.toReview
import com.example.myapplication.data.dto.toUser
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
}

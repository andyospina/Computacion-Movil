package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreProductData
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/**
 * Pruebas de [ReviewRepository] con el DataSource simulado (MockK): ninguna toca Firebase.
 * Cubren el mapeo DTO -> modelo de dominio, los errores y las reglas de los likes.
 */
class ReviewRepositoryTest {

    private val remote = mockk<ReviewRemoteDataSource>()
    private val products = mockk<ProductRemoteDataSource>()
    private val users = mockk<UserRepository>()
    private val session = mockk<CurrentUserIdProvider>()
    private val repository = ReviewRepository(remote, products, users, session)

    private fun dto(
        id: String = "r1",
        likedBy: List<String> = emptyList(),
        author: FirestoreAuthorDto = FirestoreAuthorDto(name = "Ana Pérez", username = "ana"),
        rating: Int = 4
    ) = FirestoreReviewDto(
        id = id,
        userId = "u1",
        productId = "p1",
        productName = "Audífonos",
        rating = rating,
        title = "Muy buenos",
        body = "Se escuchan genial",
        recommended = rating >= 4,
        user = author,
        likedBy = likedBy,
        createdAt = Date(1_700_000_000_000)
    )

    private fun review(likedByMe: Boolean) = Review(
        id = "r1", productId = "p1", author = "Ana", verifiedBuyer = false, rating = 4,
        title = "t", body = "b", date = "", usefulCount = 0, recommended = true,
        userId = "u1", likesCount = 1, likedByMe = likedByMe
    )

    // ---- Mapeos DTO -> modelo ----

    @Test
    fun getReviewById_mapsTheDocumentToTheDomainModel() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.getReviewById("r1") } returns dto(likedBy = listOf("otro", "me"))

        val review = (repository.getReviewById("r1") as Result.Success).data

        assertEquals("r1", review.id)
        assertEquals("Ana Pérez", review.author)
        assertEquals("Audífonos", review.productName)
        assertEquals("u1", review.userId)
        assertEquals(4, review.rating)
        assertTrue(review.recommended)
        assertEquals(2, review.likesCount)
        assertTrue("el usuario de la sesión está en likedBy", review.likedByMe)
        assertTrue(review.date.isNotBlank())
    }

    @Test
    fun getReviewById_authorWithoutName_fallsBackToUsername() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.getReviewById("r1") } returns dto(author = FirestoreAuthorDto(name = "", username = "ana_dev"))

        val review = (repository.getReviewById("r1") as Result.Success).data

        assertEquals("ana_dev", review.author)
    }

    @Test
    fun getReviewById_withoutSession_isNeverLikedByMe() = runTest {
        every { session.currentUserId() } returns null
        coEvery { remote.getReviewById("r1") } returns dto(likedBy = listOf("me"))

        val review = (repository.getReviewById("r1") as Result.Success).data

        assertFalse(review.likedByMe)
        assertEquals(1, review.likesCount)
    }

    @Test
    fun getReviewsByProduct_mapsEveryReviewKeepingTheDataSourceOrder() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.getReviewsByProduct("p1") } returns listOf(dto(id = "nueva"), dto(id = "vieja", rating = 2))

        val reviews = (repository.getReviewsByProduct("p1") as Result.Success).data

        assertEquals(listOf("nueva", "vieja"), reviews.map { it.id })
        assertEquals(listOf(true, false), reviews.map { it.recommended })
    }

    // ---- Errores ----

    @Test
    fun getReviewById_whenTheDocumentDoesNotExist_returnsFailureWithNotFoundMessage() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.getReviewById("x") } throws NotFoundException("reviews/x no existe")

        val failure = repository.getReviewById("x") as Result.Failure

        assertEquals(R.string.error_api_not_found, failure.messageRes)
        assertTrue(failure.exception is NotFoundException)
    }

    // ---- Likes ----

    @Test
    fun toggleLike_whenNotLikedYet_likesTheReviewWithTheSessionUser() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.likeReview("r1", "me") } returns Unit

        val result = repository.toggleLike(review(likedByMe = false))

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { remote.likeReview("r1", "me") }
        coVerify(exactly = 0) { remote.unlikeReview(any(), any()) }
    }

    @Test
    fun toggleLike_whenAlreadyLiked_removesTheLike() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { remote.unlikeReview("r1", "me") } returns Unit

        val result = repository.toggleLike(review(likedByMe = true))

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { remote.unlikeReview("r1", "me") }
        coVerify(exactly = 0) { remote.likeReview(any(), any()) }
    }

    @Test
    fun toggleLike_withoutSession_failsAndNeverCallsTheDataSource() = runTest {
        every { session.currentUserId() } returns null

        val failure = repository.toggleLike(review(likedByMe = false)) as Result.Failure

        assertEquals(R.string.error_session_required, failure.messageRes)
        coVerify(exactly = 0) { remote.likeReview(any(), any()) }
        coVerify(exactly = 0) { remote.unlikeReview(any(), any()) }
    }

    // ---- Mapeo modelo -> DTO ----

    @Test
    fun createReview_copiesAuthorAndProduct_trimsTextAndComputesRecommended() = runTest {
        val author = User(
            name = "Ana Pérez", initials = "AP", memberSinceYear = 2024, reviewCount = 0, usefulVotes = 0,
            averageRating = 0.0, profileImage = "http://foto", username = "ana", id = "u1"
        )
        coEvery { users.getUserById("u1") } returns Result.Success(author)
        coEvery { products.getProductById("p1") } returns
            FirestoreProductData(FirestoreProductDto("p1", "Audífonos", "Electrónica"), emptyList())
        val sent = slot<NewReviewDto>()
        coEvery { remote.createReview(capture(sent)) } returns Unit

        val result = repository.createReview("p1", "u1", rating = 4, title = "  Muy buenos ", body = " Genial\n")

        assertTrue(result is Result.Success)
        with(sent.captured) {
            assertEquals("Ana Pérez", authorName)
            assertEquals("ana", authorUsername)
            assertEquals("http://foto", authorImage)
            assertEquals("Audífonos", productName)
            assertEquals("Muy buenos", title)
            assertEquals("Genial", body)
            assertTrue("4 estrellas o más se marca como recomendada", recommended)
        }
    }

    // ---- Funcionalidad adicional: «Reseñas que me gustaron» ----

    @Test
    fun observeMyLikedReviews_mapsTheLikedReviewsAsLikedByMe() = runTest {
        every { session.currentUserId() } returns "me"
        every { remote.observeReviewsLikedBy("me") } returns flowOf(listOf(dto(likedBy = listOf("me"))))

        val result = repository.observeMyLikedReviews().first() as Result.Success

        assertEquals(1, result.data.size)
        assertTrue(result.data.single().likedByMe)
    }

    @Test
    fun observeMyLikedReviews_withoutSession_emitsSessionRequiredFailure() = runTest {
        every { session.currentUserId() } returns null

        val failure = repository.observeMyLikedReviews().first() as Result.Failure

        assertEquals(R.string.error_session_required, failure.messageRes)
    }
}

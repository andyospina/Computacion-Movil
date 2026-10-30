package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.datasource.impl.FollowFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ProductFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ReviewFirestoreDataSource
import com.example.myapplication.data.datasource.impl.UserFirestoreDataSource
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.NotAuthenticatedException
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.Seeder
import com.example.myapplication.support.FirestoreEmulatorTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Pruebas de integración de los Repository: usan los DataSource de Firestore reales contra el emulador
 * y, donde hace falta aislar un fallo concreto, un DataSource falso.
 */
class RepositoryIntegrationTest : FirestoreEmulatorTest() {

    private class FakeSession(var uid: String?) : CurrentUserIdProvider {
        override fun currentUserId(): String? = uid
    }

    private class FailingReviewDataSource(private val error: Exception) : ReviewRemoteDataSource {
        override suspend fun getReviewsByProduct(productId: String): List<ReviewData> = throw error
        override suspend fun getReviewsByUser(userId: String): List<ReviewData> = throw error
        override suspend fun getReviewById(id: String): ReviewData = throw error
        override suspend fun createReview(review: NewReviewDto) = throw error
        override suspend fun updateReview(id: String, review: UpdateReviewDto) = throw error
        override suspend fun deleteReview(id: String) = throw error
        override fun observeReviewsByProduct(productId: String): Flow<List<ReviewData>> = emptyFlow()
        override fun observeReviewsByUsers(userIds: List<String>): Flow<List<ReviewData>> = emptyFlow()
        override suspend fun likeReview(reviewId: String, userId: String) = throw error
        override suspend fun unlikeReview(reviewId: String, userId: String) = throw error
    }

    private val seeder = Seeder(seed = 23)
    private val author = seeder.user()
    private val fan = seeder.user()
    private val product = seeder.product()
    private val session = FakeSession(uid = null)

    private lateinit var userRepository: UserRepository
    private lateinit var reviewRepository: ReviewRepository

    @Before
    fun prepareRepositories() {
        userRepository = UserRepository(UserFirestoreDataSource(firestore))
        reviewRepository = ReviewRepository(
            ReviewFirestoreDataSource(firestore),
            ProductFirestoreDataSource(firestore),
            userRepository,
            session
        )
        seed(SeedDataset(listOf(author, fan), listOf(product), emptyList(), emptyList()))
    }

    private fun <T> Result<T>.successData(): T = (this as? Result.Success)?.data ?: error("Se esperaba Success y fue $this")

    private fun Result<*>.failureMessage(): Int = (this as? Result.Failure)?.messageRes ?: error("Se esperaba Failure y fue $this")

    @Test
    fun createReview_copiesAuthorAndProductIntoTheDocument() = test {
        val result = reviewRepository.createReview(product.id, author.id, rating = 5, title = "  Excelente ", body = " Muy bueno ")

        assertTrue(result is Result.Success)
        val stored = firestore.collection("reviews").get().await().documents.single()
        assertEquals("Excelente", stored.getString("title"))
        assertEquals(product.name, stored.getString("productName"))
        assertEquals(author.name, stored.getString("user.name"))
        assertEquals(true, stored.getBoolean("recommended"))
    }

    @Test
    fun getReviewById_mapsTheDocumentToTheDomainModel() = test {
        session.uid = fan.id
        val review = seeder.review(author, product, likers = listOf(fan)).copy(rating = 3, recommended = false)
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))

        val model = reviewRepository.getReviewById(review.id).successData()

        assertEquals(review.title, model.title)
        assertEquals(author.name, model.author)
        assertEquals(product.name, model.productName)
        assertEquals(1, model.likesCount)
        assertTrue("el usuario de la sesión dio like", model.likedByMe)
        assertFalse(model.recommended)
        assertEquals(3, model.rating)
        assertTrue(model.date.isNotBlank())
    }

    @Test
    fun createReview_withUnknownAuthorOrProduct_failsAndStoresNothing() = test {
        val noAuthor = reviewRepository.createReview(product.id, "fantasma", 4, "t", "b")
        val noProduct = reviewRepository.createReview("producto-fantasma", author.id, 4, "t", "b")

        assertEquals(R.string.error_api_not_found, noAuthor.failureMessage())
        assertEquals(R.string.error_api_not_found, noProduct.failureMessage())
        assertTrue(firestore.collection("reviews").get().await().isEmpty)
    }

    @Test
    fun getReviewsByUser_filtersByAuthor() = test {
        val mine = seeder.review(author, product)
        val theirs = seeder.review(fan, product)
        seed(SeedDataset(emptyList(), emptyList(), listOf(mine, theirs), emptyList()))

        val result = reviewRepository.getReviewsByUser(author.id).successData()

        assertEquals(listOf(mine.id), result.map { it.id })
    }

    @Test
    fun toggleLike_isReflectedOnTheNextRead_andOnTheLiveFlow() = test {
        session.uid = fan.id
        val review = seeder.review(author, product, likers = emptyList())
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))
        val initial = reviewRepository.getReviewById(review.id).successData()

        reviewRepository.toggleLike(initial).successData()
        val liked = reviewRepository.getReviewById(review.id).successData()
        assertTrue(liked.likedByMe)
        assertEquals(1, liked.likesCount)

        // El flujo en tiempo real ya trae el like y, al repetir toggleLike, lo quita.
        val live = reviewRepository.observeReviewsByProduct(product.id).first { it is Result.Success && it.data.any { r -> r.likedByMe } }
        assertEquals(1, live.successData().single().likesCount)
        reviewRepository.toggleLike(liked).successData()
        assertEquals(0, reviewRepository.getReviewById(review.id).successData().likesCount)
    }

    @Test
    fun toggleLike_withoutSession_failsWithAuthenticationError() = test {
        session.uid = null
        val review = seeder.review(author, product)
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))
        val model = reviewRepository.getReviewById(review.id).successData()

        val failure = reviewRepository.toggleLike(model) as Result.Failure

        assertTrue(failure.exception is NotAuthenticatedException)
        assertEquals(R.string.error_session_required, failure.messageRes)
    }

    @Test
    fun offline_readsFailWithUnreachableMessage_thenRecoverWhenOnlineAgain() = test {
        val review = seeder.review(author, product)
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))
        firestore.disableNetwork().await()

        assertEquals(R.string.error_api_unreachable, reviewRepository.getReviewById(review.id).failureMessage())

        firestore.enableNetwork().await()
        assertEquals(review.id, reviewRepository.getReviewById(review.id).successData().id)
    }

    @Test
    fun dataSourceErrors_areMappedToResultFailures() = test {
        fun repoWith(error: Exception) =
            ReviewRepository(FailingReviewDataSource(error), ProductFirestoreDataSource(firestore), userRepository, session)

        val unreachable = repoWith(IOException("sin red")).getReviewsByProduct(product.id)
        val missing = repoWith(NotFoundException("reviews/x no existe")).getReviewById("x")
        val unexpected = repoWith(IllegalStateException("boom")).deleteReview("x")

        assertEquals(R.string.error_api_unreachable, unreachable.failureMessage())
        assertEquals(R.string.error_api_not_found, missing.failureMessage())
        assertEquals(R.string.error_api_unexpected, unexpected.failureMessage())
        assertTrue((unreachable as Result.Failure).exception is IOException)
    }

    @Test
    fun getUsersByIds_keepsRequestedOrderAndOmitsMissing() = test {
        val result = userRepository.getUsersByIds(listOf(fan.id, "fantasma", author.id)).successData()

        assertEquals(listOf(fan.id, author.id), result.map { it.id })
    }

    @Test
    fun getProducts_returnsAverageRatingAndRecommendationFromReviews() = test {
        val reviews = listOf(5, 3).map { seeder.review(author, product).copy(rating = it, recommended = it >= 4) }
        seed(SeedDataset(emptyList(), emptyList(), reviews, emptyList()))

        val model = ProductRepository(ProductFirestoreDataSource(firestore)).getProducts().successData().single()

        assertEquals(4.0, model.rating, 0.001)
    }

    @Test
    fun followRepository_followsAndUnfollows_withLiveStats_andRejectsSelfFollow() = test {
        session.uid = fan.id
        val follows = FollowRepository(FollowFirestoreDataSource(firestore), session)

        follows.follow(author.id).successData()
        val stats = follows.observeFollowStats(author.id).first { it is Result.Success && it.data.followerIds.isNotEmpty() }.successData()
        assertEquals(listOf(fan.id), stats.followerIds)

        follows.unfollow(author.id).successData()
        assertTrue(follows.observeFollowStats(author.id).first().successData().followerIds.isEmpty())

        assertTrue(follows.follow(fan.id) is Result.Failure)
    }
}

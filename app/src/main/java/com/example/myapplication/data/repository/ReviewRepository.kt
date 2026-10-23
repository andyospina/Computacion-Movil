package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MIN_RECOMMENDED_RATING = 4

class ReviewRepository @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource,
    private val productDataSource: ProductRemoteDataSource,
    private val userRepository: UserRepository,
    private val currentUserId: CurrentUserIdProvider
) {

    suspend fun getReviewsByProduct(productId: String): Result<List<Review>> = runCatchingApi {
        remoteDataSource.getReviewsByProduct(productId).map { it.toReview(currentUserId.currentUserId()) }
    }

    suspend fun getReviewsByUser(userId: String): Result<List<Review>> = runCatchingApi {
        remoteDataSource.getReviewsByUser(userId).map { it.toReview(currentUserId.currentUserId()) }
    }

    suspend fun getReviewById(id: String): Result<Review> = runCatchingApi {
        remoteDataSource.getReviewById(id).toReview(currentUserId.currentUserId())
    }

    /** Reseñas del producto en tiempo real: un like o una reseña nueva vuelven a emitir la lista. */
    fun observeReviewsByProduct(productId: String): Flow<Result<List<Review>>> =
        remoteDataSource.observeReviewsByProduct(productId)
            .map { list -> list.map { it.toReview(currentUserId.currentUserId()) } }
            .asResult()

    fun observeReviewsByUsers(userIds: List<String>): Flow<Result<List<Review>>> =
        remoteDataSource.observeReviewsByUsers(userIds)
            .map { list -> list.map { it.toReview(currentUserId.currentUserId()) } }
            .asResult()

    /** Da like si el usuario actual aún no lo había dado, o lo quita si ya lo había dado. */
    suspend fun toggleLike(review: Review): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        if (review.likedByMe) remoteDataSource.unlikeReview(review.id, uid)
        else remoteDataSource.likeReview(review.id, uid)
    }

    /** Copia en la reseña los datos del autor y del producto que su tarjeta necesita mostrar. */
    suspend fun createReview(
        productId: String,
        userId: String,
        rating: Int,
        title: String,
        body: String
    ): Result<Unit> {
        val author = when (val result = userRepository.getUserById(userId)) {
            is Result.Success -> result.data
            is Result.Failure -> return result
        }
        return runCatchingApi {
            val product = productDataSource.getProductById(productId).toProduct()
            remoteDataSource.createReview(
                NewReviewDto(
                    productId = productId,
                    productName = product.name,
                    userId = userId,
                    authorName = author.name,
                    authorUsername = author.username,
                    authorImage = author.profileImage,
                    rating = rating,
                    title = title.trim(),
                    body = body.trim(),
                    recommended = rating >= MIN_RECOMMENDED_RATING
                )
            )
        }
    }

    suspend fun updateReview(
        id: String,
        rating: Int,
        title: String,
        body: String
    ): Result<Unit> = runCatchingApi {
        remoteDataSource.updateReview(
            id,
            UpdateReviewDto(
                rating = rating,
                title = title.trim(),
                body = body.trim(),
                recommended = rating >= MIN_RECOMMENDED_RATING
            )
        )
    }

    suspend fun deleteReview(id: String): Result<Unit> = runCatchingApi {
        remoteDataSource.deleteReview(id)
    }
}

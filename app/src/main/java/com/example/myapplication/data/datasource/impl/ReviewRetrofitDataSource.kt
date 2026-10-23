package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.ApiConfig
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.CreateReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.services.ReviewRetrofitService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewRetrofitDataSource @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    // El backend no filtra GET /reviews por producto ni por usuario: se filtra aquí.
    override suspend fun getReviewsByProduct(productId: String): List<ReviewData> =
        service.getReviews().filter { it.productId.toString() == productId }.newestFirst()

    override suspend fun getReviewsByUser(userId: String): List<ReviewData> =
        service.getReviews().filter { it.userId.toString() == userId }.newestFirst()

    override suspend fun getReviewById(id: String): ReviewData = service.getReviewById(id)

    // El backend ya conoce al autor por su id numérico; los datos desnormalizados no se envían.
    override suspend fun createReview(review: NewReviewDto) {
        service.createReview(
            CreateReviewDto(
                rating = review.rating,
                title = review.title,
                body = review.body,
                recommended = review.recommended,
                userId = review.userId.toIntOrNull() ?: ApiConfig.TEMP_CURRENT_USER_ID.toInt(),
                productId = review.productId.toInt()
            )
        )
    }

    override suspend fun updateReview(id: String, review: UpdateReviewDto) {
        service.updateReview(id, review)
    }

    override suspend fun deleteReview(id: String) = service.deleteReview(id)

    override fun observeReviewsByProduct(productId: String): Flow<List<ReviewData>> =
        pollingFlow { getReviewsByProduct(productId) }

    override fun observeReviewsByUsers(userIds: List<String>): Flow<List<ReviewData>> =
        pollingFlow { service.getReviews().filter { it.userId.toString() in userIds }.newestFirst() }

    override suspend fun likeReview(reviewId: String, userId: String) {
        throw UnsupportedOperationException("El backend Express no tiene likes")
    }

    override suspend fun unlikeReview(reviewId: String, userId: String) {
        throw UnsupportedOperationException("El backend Express no tiene likes")
    }

    // createdAt es ISO-8601 en UTC, así que el orden alfabético es el cronológico
    private fun List<ReviewDto>.newestFirst() =
        sortedWith(compareByDescending<ReviewDto> { it.createdAt.orEmpty() }.thenByDescending { it.id })
}

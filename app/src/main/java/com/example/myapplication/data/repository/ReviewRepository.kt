package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.CreateReviewDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.dto.toReview
import javax.inject.Inject

private const val MIN_RECOMMENDED_RATING = 4

class ReviewRepository @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource
) {

    // El backend no filtra GET /reviews por producto ni por usuario: se filtra aquí.
    suspend fun getReviewsByProduct(productId: String): Result<List<Review>> = runCatchingApi {
        remoteDataSource.getReviews()
            .filter { it.productId.toString() == productId }
            .newestFirst()
            .map { it.toReview() }
    }

    suspend fun getReviewsByUser(userId: String): Result<List<Review>> = runCatchingApi {
        remoteDataSource.getReviews()
            .filter { it.userId.toString() == userId }
            .newestFirst()
            .map { it.toReview() }
    }

    suspend fun getReviewById(id: String): Result<Review> = runCatchingApi {
        remoteDataSource.getReviewById(id).toReview()
    }

    suspend fun createReview(
        productId: String,
        userId: String,
        rating: Int,
        title: String,
        body: String
    ): Result<Unit> = runCatchingApi {
        val dto = CreateReviewDto(
            rating = rating,
            title = title.trim(),
            body = body.trim(),
            recommended = rating >= MIN_RECOMMENDED_RATING,
            userId = userId.toInt(),
            productId = productId.toInt()
        )
        remoteDataSource.createReview(dto)
        Unit
    }

    suspend fun updateReview(
        id: String,
        rating: Int,
        title: String,
        body: String
    ): Result<Unit> = runCatchingApi {
        val dto = UpdateReviewDto(
            rating = rating,
            title = title.trim(),
            body = body.trim(),
            recommended = rating >= MIN_RECOMMENDED_RATING
        )
        remoteDataSource.updateReview(id, dto)
        Unit
    }

    suspend fun deleteReview(id: String): Result<Unit> = runCatchingApi {
        remoteDataSource.deleteReview(id)
    }

    // createdAt es ISO-8601 en UTC, así que el orden alfabético es el cronológico
    private fun List<ReviewDto>.newestFirst() =
        sortedWith(compareByDescending<ReviewDto> { it.createdAt.orEmpty() }.thenByDescending { it.id })
}

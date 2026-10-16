package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto

/** Las listas se devuelven de la más reciente a la más antigua. */
interface ReviewRemoteDataSource {
    suspend fun getReviewsByProduct(productId: String): List<ReviewData>
    suspend fun getReviewsByUser(userId: String): List<ReviewData>
    suspend fun getReviewById(id: String): ReviewData
    suspend fun createReview(review: NewReviewDto)
    suspend fun updateReview(id: String, review: UpdateReviewDto)
    suspend fun deleteReview(id: String)
}

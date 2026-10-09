package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.CreateReviewDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto

interface ReviewRemoteDataSource {
    suspend fun getReviews(): List<ReviewDto>
    suspend fun getReviewById(id: String): ReviewDto
    suspend fun createReview(review: CreateReviewDto): ReviewDto
    suspend fun updateReview(id: String, review: UpdateReviewDto): ReviewDto
    suspend fun deleteReview(id: String)
}

package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.CreateReviewDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.services.ReviewRetrofitService
import javax.inject.Inject

class ReviewRetrofitDataSource @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    override suspend fun getReviews(): List<ReviewDto> = service.getReviews()

    override suspend fun getReviewById(id: String): ReviewDto = service.getReviewById(id)

    override suspend fun createReview(review: CreateReviewDto): ReviewDto = service.createReview(review)

    override suspend fun updateReview(id: String, review: UpdateReviewDto): ReviewDto =
        service.updateReview(id, review)

    override suspend fun deleteReview(id: String) = service.deleteReview(id)
}

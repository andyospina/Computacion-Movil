package com.example.myapplication.data.services

import com.example.myapplication.data.dto.CreateReviewDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.UpdateReviewDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewRetrofitService {

    @GET("reviews")
    suspend fun getReviews(): List<ReviewDto>

    @GET("reviews/{id}")
    suspend fun getReviewById(@Path("id") id: String): ReviewDto

    @POST("reviews")
    suspend fun createReview(@Body review: CreateReviewDto): ReviewDto

    @PUT("reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body review: UpdateReviewDto): ReviewDto

    // El backend responde 204 sin cuerpo; Retrofit lo acepta con Unit.
    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String)
}

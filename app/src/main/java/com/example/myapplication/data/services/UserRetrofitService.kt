package com.example.myapplication.data.services

import com.example.myapplication.data.dto.UserProfileDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRetrofitService {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserProfileDto
}

package com.example.myapplication.data.services

import com.example.myapplication.data.dto.UserProfileDto
import com.example.myapplication.data.dto.UserUpdateBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserRetrofitService {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserProfileDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body body: UserUpdateBody): UserProfileDto
}

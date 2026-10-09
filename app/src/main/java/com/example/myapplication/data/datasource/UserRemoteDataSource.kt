package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.UserProfileDto

interface UserRemoteDataSource {
    suspend fun getUserById(id: String): UserProfileDto
}

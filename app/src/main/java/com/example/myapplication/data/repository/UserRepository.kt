package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.User
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) {

    suspend fun getUserById(id: String): Result<User> = runCatchingApi {
        remoteDataSource.getUserById(id).toUser()
    }

    suspend fun registerUser(
        userId: String,
        username: String,
        name: String,
        email: String,
        country: String,
        bio: String
    ): Result<Unit> = runCatchingApi {
        remoteDataSource.registerUser(
            userId,
            RegisterUserDto(username = username, name = name, email = email, country = country, bio = bio)
        )
    }

    suspend fun updateUser(userId: String, name: String, country: String, bio: String): Result<Unit> =
        runCatchingApi {
            remoteDataSource.updateUser(
                userId,
                UpdateUserDto(name = name.trim(), country = country.trim(), bio = bio.trim())
            )
        }

    suspend fun updateProfileImage(userId: String, imageUrl: String): Result<Unit> = runCatchingApi {
        remoteDataSource.updateProfileImage(userId, imageUrl)
    }
}

package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.User
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.toUser
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) {

    suspend fun getUserById(id: String): Result<User> = runCatchingApi {
        remoteDataSource.getUserById(id).toUser()
    }
}

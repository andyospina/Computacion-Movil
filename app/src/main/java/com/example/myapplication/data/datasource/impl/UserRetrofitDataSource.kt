package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.UserProfileDto
import com.example.myapplication.data.services.UserRetrofitService
import javax.inject.Inject

class UserRetrofitDataSource @Inject constructor(
    private val service: UserRetrofitService
) : UserRemoteDataSource {

    override suspend fun getUserById(id: String): UserProfileDto = service.getUserById(id)
}

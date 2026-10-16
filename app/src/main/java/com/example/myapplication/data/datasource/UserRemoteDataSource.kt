package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import com.example.myapplication.data.dto.UserDto

interface UserRemoteDataSource {
    suspend fun getUserById(id: String): UserDto

    /** Guarda los datos adicionales de un usuario ya creado en Firebase Authentication. */
    suspend fun registerUser(userId: String, user: RegisterUserDto)

    suspend fun updateUser(userId: String, user: UpdateUserDto)

    suspend fun updateProfileImage(userId: String, imageUrl: String)
}

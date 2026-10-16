package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import com.example.myapplication.data.dto.UserDto
import com.example.myapplication.data.dto.UserUpdateBody
import com.example.myapplication.data.services.UserRetrofitService
import javax.inject.Inject

class UserRetrofitDataSource @Inject constructor(
    private val service: UserRetrofitService
) : UserRemoteDataSource {

    override suspend fun getUserById(id: String): UserDto = service.getUserById(id)

    /**
     * No soportado: `POST /users` del backend exige una contraseña y asigna un id numérico propio,
     * pero el registro lo hace Firebase Authentication y la contraseña no debe guardarse aparte.
     */
    override suspend fun registerUser(userId: String, user: RegisterUserDto) {
        throw UnsupportedOperationException("El backend Express no registra usuarios de Firebase")
    }

    // El backend no tiene campo de país: solo se envían el nombre y la biografía.
    override suspend fun updateUser(userId: String, user: UpdateUserDto) {
        service.updateUser(userId, UserUpdateBody(name = user.name, bio = user.bio))
    }

    override suspend fun updateProfileImage(userId: String, imageUrl: String) {
        service.updateUser(userId, UserUpdateBody(profileImage = imageUrl))
    }
}

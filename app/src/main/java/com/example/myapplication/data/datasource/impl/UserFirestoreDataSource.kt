package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.FirestoreUserDto
import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import com.example.myapplication.data.dto.UserDto
import com.example.myapplication.data.dto.toFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val USERS_COLLECTION = "users"

class UserFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    override suspend fun getUserById(id: String): UserDto {
        val snapshot = firestore.collection(USERS_COLLECTION).document(id).get().await()
        if (!snapshot.exists()) throw NotFoundException("users/$id no existe")
        return snapshot.toObject(FirestoreUserDto::class.java)
            ?: throw NotFoundException("users/$id no se pudo convertir")
    }

    // El id del documento es el uid de Firebase Authentication: users/{uid}
    override suspend fun registerUser(userId: String, user: RegisterUserDto) {
        firestore.collection(USERS_COLLECTION).document(userId).set(user.toFirestoreDto()).await()
    }

    // update() solo toca estos campos: username, correo y fecha de alta se conservan.
    // Las reseñas ya publicadas guardan una copia del nombre anterior (no se sincroniza).
    override suspend fun updateUser(userId: String, user: UpdateUserDto) {
        firestore.collection(USERS_COLLECTION).document(userId).update(
            mapOf("name" to user.name, "country" to user.country, "bio" to user.bio)
        ).await()
    }

    override suspend fun updateProfileImage(userId: String, imageUrl: String) {
        firestore.collection(USERS_COLLECTION).document(userId).update("profileImage", imageUrl).await()
    }
}

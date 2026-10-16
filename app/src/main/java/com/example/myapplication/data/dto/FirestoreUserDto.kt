package com.example.myapplication.data.dto

import com.example.myapplication.data.User
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Calendar
import java.util.Date

/**
 * Documento `users/{uid}`. Todos los campos tienen valor por defecto porque Firestore necesita un
 * constructor sin argumentos para deserializar, y para tolerar documentos incompletos.
 */
data class FirestoreUserDto(
    @DocumentId val id: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val country: String = "",
    val bio: String = "",
    val profileImage: String? = null,
    @ServerTimestamp val createdAt: Date? = null
) : UserDto {

    override fun toUser(): User = User(
        name = name,
        initials = initialsOf(name),
        memberSinceYear = createdAt?.let { Calendar.getInstance().apply { time = it }.get(Calendar.YEAR) } ?: 0,
        reviewCount = 0,
        usefulVotes = 0,
        averageRating = 0.0,
        profileImage = profileImage,
        username = username,
        bio = bio,
        country = country
    )
}

fun RegisterUserDto.toFirestoreDto() = FirestoreUserDto(
    username = username.trim(),
    name = name.trim(),
    email = email.trim(),
    country = country.trim(),
    bio = bio.trim()
)

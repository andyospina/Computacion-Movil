package com.example.myapplication.data.dto

import com.example.myapplication.data.User
import kotlinx.serialization.Serializable

/** Respuesta de GET /users/{id} (el backend nunca incluye `password`). */
@Serializable
data class UserProfileDto(
    val id: Int,
    val username: String = "",
    val email: String = "",
    val name: String,
    val profileImage: String? = null,
    val website: String? = null,
    val bio: String? = null,
    val followersCount: Int = 0,
    val createdAt: String? = null
)

/**
 * El backend no guarda estadísticas de reseñas ni votos útiles: reviewCount y averageRating se
 * calculan después con las reseñas del usuario y usefulVotes queda en 0.
 */
fun UserProfileDto.toUser(): User = User(
    name = name,
    initials = name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() },
    memberSinceYear = createdAt.toYear(),
    reviewCount = 0,
    usefulVotes = 0,
    averageRating = 0.0,
    profileImage = profileImage
)

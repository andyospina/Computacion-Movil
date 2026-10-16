package com.example.myapplication.data.dto

import com.example.myapplication.data.User
import kotlinx.serialization.Serializable

/** Cuerpo de PUT /users/{id}: los campos null no se envían y el backend los deja como están. */
@Serializable
data class UserUpdateBody(
    val name: String? = null,
    val bio: String? = null,
    val profileImage: String? = null
)

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
) : UserDto {

    /**
     * El backend no guarda estadísticas de reseñas ni votos útiles: reviewCount y averageRating se
     * calculan después con las reseñas del usuario y usefulVotes queda en 0.
     */
    override fun toUser(): User = User(
        name = name,
        initials = initialsOf(name),
        memberSinceYear = createdAt.toYear(),
        reviewCount = 0,
        usefulVotes = 0,
        averageRating = 0.0,
        profileImage = profileImage,
        username = username,
        bio = bio.orEmpty()
    )
}

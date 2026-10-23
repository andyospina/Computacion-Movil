package com.example.myapplication.data

data class User(
    val name: String,
    val initials: String,
    val memberSinceYear: Int,
    val reviewCount: Int,
    val usefulVotes: Int,
    val averageRating: Double,
    val profileImage: String? = null,
    val username: String = "",
    val bio: String = "",
    val country: String = "",
    val id: String = ""
)

/**
 * Estadísticas calculadas con las reseñas del usuario: cantidad, promedio y likes recibidos
 * (se muestran en la casilla de "votos").
 */
fun User.withReviewStats(reviews: List<Review>): User = copy(
    reviewCount = reviews.size,
    usefulVotes = reviews.sumOf { it.likesCount },
    averageRating = if (reviews.isEmpty()) 0.0
    else Math.round(reviews.map { it.rating }.average() * 10) / 10.0
)

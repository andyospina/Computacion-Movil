package com.example.myapplication.data

enum class ReviewStatus {
    PUBLISHED,
    DRAFT
}

data class Review(
    val id: String,
    val productId: String,
    val author: String,
    val verifiedBuyer: Boolean,
    val rating: Int,
    val title: String,
    val body: String,
    val date: String,
    val usefulCount: Int,
    val recommended: Boolean,
    val hasPhoto: Boolean = false,
    val status: ReviewStatus = ReviewStatus.PUBLISHED,
    val userId: String = "",
    val productName: String = "",
    val likesCount: Int = 0,
    // Si el usuario de la sesión actual ya le dio like (para pintar el corazón lleno o vacío)
    val likedByMe: Boolean = false
)

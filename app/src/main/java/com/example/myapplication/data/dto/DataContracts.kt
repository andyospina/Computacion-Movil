package com.example.myapplication.data.dto

import com.example.myapplication.data.AppNotification
import com.example.myapplication.data.Product
import com.example.myapplication.data.Review

/** Abstracción común de las respuestas de producto de cada backend (Retrofit, Firestore). */
interface ProductData {
    fun toProduct(): Product
}

/** Abstracción común de las respuestas de reseña de cada backend (Retrofit, Firestore). */
interface ReviewData {
    /** [currentUserId] permite saber si el usuario de la sesión ya le dio like a la reseña. */
    fun toReview(currentUserId: String? = null): Review
}

/** Abstracción común de las notificaciones de la bandeja de cada backend. */
interface NotificationData {
    fun toNotification(): AppNotification
}

/** Datos de una reseña nueva, comunes a los dos backends. El autor se desnormaliza en la reseña. */
data class NewReviewDto(
    val productId: String,
    val productName: String,
    val userId: String,
    val authorName: String,
    val authorUsername: String,
    val authorImage: String?,
    val rating: Int,
    val title: String,
    val body: String,
    val recommended: Boolean
)

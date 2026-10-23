package com.example.myapplication.data

enum class NotificationType {
    LIKE,
    FOLLOW,
    UNKNOWN
}

/** Notificación de la bandeja del usuario. La crea una Cloud Function; la app solo la lee y la marca. */
data class AppNotification(
    val id: String,
    val type: NotificationType,
    val actorId: String,
    val actorName: String,
    val reviewTitle: String,
    val productId: String,
    val date: String,
    val read: Boolean
)

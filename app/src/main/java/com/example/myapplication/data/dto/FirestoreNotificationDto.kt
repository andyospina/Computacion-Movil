package com.example.myapplication.data.dto

import com.example.myapplication.data.AppNotification
import com.example.myapplication.data.NotificationType
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Documento `users/{uid}/notifications/{id}`. Lo escribe la Cloud Function (Admin SDK) cuando alguien
 * da like a una reseña o empieza a seguir al usuario; la app solo lo lee y cambia `read`.
 */
data class FirestoreNotificationDto(
    @DocumentId val id: String = "",
    val type: String = "",
    val actorId: String = "",
    val actorName: String = "",
    val reviewId: String = "",
    val reviewTitle: String = "",
    val productId: String = "",
    val read: Boolean = false,
    @ServerTimestamp val createdAt: Date? = null
) : NotificationData {

    override fun toNotification(): AppNotification = AppNotification(
        id = id,
        type = when (type) {
            TYPE_LIKE -> NotificationType.LIKE
            TYPE_FOLLOW -> NotificationType.FOLLOW
            else -> NotificationType.UNKNOWN
        },
        actorId = actorId,
        actorName = actorName,
        reviewTitle = reviewTitle,
        productId = productId,
        date = createdAt.toDisplayDate(),
        read = read
    )

    companion object {
        const val TYPE_LIKE = "like"
        const val TYPE_FOLLOW = "follow"
    }
}

/** Documento `users/{uid}/fcmTokens/{token}`: un documento por dispositivo donde el usuario inició sesión. */
data class FirestoreDeviceTokenDto(
    val token: String = "",
    val platform: String = "android",
    @ServerTimestamp val updatedAt: Date? = null
)

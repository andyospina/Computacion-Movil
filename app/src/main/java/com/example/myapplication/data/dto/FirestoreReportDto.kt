package com.example.myapplication.data.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Documento `reports/{userId_reviewId}`: [userId] reportó la reseña [reviewId] por [reason]. El id
 * compuesto (mismo patrón que `saved` y `follows`) impide reportar dos veces la misma reseña.
 */
data class FirestoreReportDto(
    val userId: String = "",
    val reviewId: String = "",
    val reason: String = "",
    @ServerTimestamp val createdAt: Date? = null
)

fun reportDocumentId(userId: String, reviewId: String) = "${userId}_$reviewId"

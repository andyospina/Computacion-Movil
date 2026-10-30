package com.example.myapplication.data.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Documento `saved/{userId_productId}`: [userId] guardó [productId]. Mismo patrón que `follows`: el id
 * compuesto impide guardar dos veces el mismo producto y permite deshacerlo sin consultar nada.
 */
data class FirestoreSavedDto(
    val userId: String = "",
    val productId: String = "",
    @ServerTimestamp val createdAt: Date? = null
)

fun savedDocumentId(userId: String, productId: String) = "${userId}_$productId"

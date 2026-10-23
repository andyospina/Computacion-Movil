package com.example.myapplication.data.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Documento `follows/{followerId_followedId}`: [followerId] sigue a [followedId]. El id compuesto
 * impide seguir dos veces a la misma persona y permite saber si existe sin hacer una consulta.
 */
data class FirestoreFollowDto(
    val followerId: String = "",
    val followedId: String = "",
    @ServerTimestamp val createdAt: Date? = null
)

fun followDocumentId(followerId: String, followedId: String) = "${followerId}_$followedId"

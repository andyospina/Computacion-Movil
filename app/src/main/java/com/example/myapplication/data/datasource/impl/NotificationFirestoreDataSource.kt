package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.NotificationRemoteDataSource
import com.example.myapplication.data.dto.FirestoreDeviceTokenDto
import com.example.myapplication.data.dto.FirestoreNotificationDto
import com.example.myapplication.data.dto.NotificationData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TOKENS_COLLECTION = "fcmTokens"
private const val NOTIFICATIONS_COLLECTION = "notifications"
private const val MAX_NOTIFICATIONS = 50L

/**
 * Los tokens van en una subcolección privada (`users/{uid}/fcmTokens`) y no en el documento del
 * usuario, que es público: así nadie más puede leerlos y un usuario puede tener varios dispositivos.
 */
class NotificationFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRemoteDataSource {

    private fun user(userId: String) = firestore.collection(USERS_COLLECTION).document(userId)

    // El token es el id del documento: registrarlo de nuevo no crea duplicados.
    override suspend fun saveDeviceToken(userId: String, token: String) {
        user(userId).collection(TOKENS_COLLECTION).document(token)
            .set(FirestoreDeviceTokenDto(token = token))
            .await()
    }

    override suspend fun deleteDeviceToken(userId: String, token: String) {
        user(userId).collection(TOKENS_COLLECTION).document(token).delete().await()
    }

    override fun observeNotifications(userId: String): Flow<List<NotificationData>> =
        user(userId).collection(NOTIFICATIONS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(MAX_NOTIFICATIONS)
            .observe()
            .map { it.toObjects(FirestoreNotificationDto::class.java) }

    override suspend fun markAsRead(userId: String, notificationIds: List<String>) {
        if (notificationIds.isEmpty()) return
        val notifications = user(userId).collection(NOTIFICATIONS_COLLECTION)
        val batch = firestore.batch()
        notificationIds.forEach { batch.update(notifications.document(it), "read", true) }
        batch.commit().await()
    }
}

package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.NotificationData
import kotlinx.coroutines.flow.Flow

interface NotificationRemoteDataSource {
    /** Registra el token FCM de este dispositivo para que el servidor pueda enviarle notificaciones. */
    suspend fun saveDeviceToken(userId: String, token: String)

    /** Al cerrar sesión: este dispositivo deja de recibir las notificaciones de ese usuario. */
    suspend fun deleteDeviceToken(userId: String, token: String)

    /** Bandeja del usuario, de la más reciente a la más antigua. */
    fun observeNotifications(userId: String): Flow<List<NotificationData>>

    suspend fun markAsRead(userId: String, notificationIds: List<String>)
}

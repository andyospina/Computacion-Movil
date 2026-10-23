package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.NotificationRemoteDataSource
import com.example.myapplication.data.dto.NotificationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** El backend Express no guarda tokens FCM ni notificaciones: la bandeja queda vacía. */
class NotificationRetrofitDataSource @Inject constructor() : NotificationRemoteDataSource {

    override suspend fun saveDeviceToken(userId: String, token: String) {
        throw UnsupportedOperationException("El backend Express no registra dispositivos")
    }

    override suspend fun deleteDeviceToken(userId: String, token: String) {
        throw UnsupportedOperationException("El backend Express no registra dispositivos")
    }

    override fun observeNotifications(userId: String): Flow<List<NotificationData>> = flowOf(emptyList())

    override suspend fun markAsRead(userId: String, notificationIds: List<String>) {
        throw UnsupportedOperationException("El backend Express no tiene notificaciones")
    }
}

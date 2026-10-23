package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.data.AppNotification
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.MessagingRemoteDataSource
import com.example.myapplication.data.datasource.NotificationRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource,
    private val messagingDataSource: MessagingRemoteDataSource,
    private val currentUserId: CurrentUserIdProvider
) {

    /** Guarda el token FCM de este dispositivo en el usuario de la sesión (al entrar a la app). */
    suspend fun registerDevice(): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        val token = messagingDataSource.getToken()
        // Para probar desde Firebase Console > Messaging > «Enviar mensaje de prueba»
        Log.d("FCM_TOKEN", token)
        remoteDataSource.saveDeviceToken(uid, token)
    }

    /** FCM rotó el token. Sin sesión no se guarda: se registrará al iniciarla. */
    suspend fun onNewToken(token: String): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: return@runCatchingApi
        remoteDataSource.saveDeviceToken(uid, token)
    }

    /** Antes de cerrar sesión, para que este dispositivo no reciba las notificaciones de otra cuenta. */
    suspend fun unregisterDevice(): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: return@runCatchingApi
        remoteDataSource.deleteDeviceToken(uid, messagingDataSource.getToken())
    }

    fun observeNotifications(): Flow<Result<List<AppNotification>>> {
        val uid = currentUserId.currentUserId()
            ?: return flowOf(Result.Failure(NotAuthenticatedException(), R.string.error_session_required))
        return remoteDataSource.observeNotifications(uid)
            .map { list -> list.map { it.toNotification() } }
            .asResult()
    }

    suspend fun markAllAsRead(notifications: List<AppNotification>): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        remoteDataSource.markAsRead(uid, notifications.filter { !it.read }.map { it.id })
    }
}

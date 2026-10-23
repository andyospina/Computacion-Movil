package com.example.myapplication.notifications

import android.util.Log
import com.example.myapplication.data.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FCM"

/** Recibe los mensajes de Firebase Cloud Messaging y los cambios del token del dispositivo. */
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // El token puede cambiar (reinstalación, borrado de datos, rotación de FCM): se vuelve a guardar.
    override fun onNewToken(token: String) {
        Log.d(TAG, "Token FCM nuevo")
        scope.launch { notificationRepository.onNewToken(token) }
    }

    // Solo llega aquí con la app en primer plano (o si el mensaje no trae bloque `notification`).
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: return
        val body = message.notification?.body ?: message.data["body"].orEmpty()
        AppNotifications.show(applicationContext, title, body)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}

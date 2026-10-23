package com.example.myapplication.data.datasource

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/** Token de registro de Firebase Cloud Messaging de esta instalación de la app. */
class MessagingRemoteDataSource @Inject constructor(
    private val messaging: FirebaseMessaging
) {

    suspend fun getToken(): String = messaging.token.await()
}

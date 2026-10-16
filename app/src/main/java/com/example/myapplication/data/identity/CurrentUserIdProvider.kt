package com.example.myapplication.data.identity

import com.example.myapplication.data.ApiConfig
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * Id del usuario actual tal como lo entiende el backend de datos seleccionado. Cambia junto con los
 * bindings de [com.example.myapplication.data.di.RemoteDataSourceModule].
 */
interface CurrentUserIdProvider {
    fun currentUserId(): String?
}

/** Firestore: el id de usuario es el uid de Firebase Authentication. */
class FirebaseCurrentUserIdProvider @Inject constructor(
    private val auth: FirebaseAuth
) : CurrentUserIdProvider {
    override fun currentUserId(): String? = auth.currentUser?.uid
}

/** Retrofit: el uid de Firebase no existe en PostgreSQL, así que se usa el usuario sembrado. */
class RetrofitCurrentUserIdProvider @Inject constructor() : CurrentUserIdProvider {
    override fun currentUserId(): String = ApiConfig.TEMP_CURRENT_USER_ID
}

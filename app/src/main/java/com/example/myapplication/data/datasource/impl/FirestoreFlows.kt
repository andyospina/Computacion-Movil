package com.example.myapplication.data.datasource.impl

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Límite de valores de Firestore para whereIn. */
internal const val WHERE_IN_LIMIT = 30

/**
 * Convierte un snapshot listener de Firestore en un Flow: emite cada vez que cambian los resultados
 * (también con las escrituras locales pendientes, así la UI responde al instante) y quita el listener
 * cuando se deja de recolectar.
 */
internal fun Query.observe(): Flow<QuerySnapshot> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (snapshot != null) trySend(snapshot)
    }
    awaitClose { registration.remove() }
}

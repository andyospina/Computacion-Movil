package com.example.myapplication.data.repository

import com.example.myapplication.data.ReportReason
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.ReportRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import javax.inject.Inject

/** Reportes de reseñas inapropiadas hechos por el usuario de la sesión. */
class ReportRepository @Inject constructor(
    private val remoteDataSource: ReportRemoteDataSource,
    private val currentUserId: CurrentUserIdProvider
) {

    /** Si el usuario de la sesión ya reportó esta reseña (sin sesión, `false`). */
    suspend fun hasReported(reviewId: String): Result<Boolean> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: return@runCatchingApi false
        remoteDataSource.hasReported(uid, reviewId)
    }

    /** Reporta la reseña. Reportar de nuevo una reseña ya reportada no es un error. */
    suspend fun report(reviewId: String, reason: ReportReason): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        if (!remoteDataSource.hasReported(uid, reviewId)) {
            remoteDataSource.report(uid, reviewId, reason)
        }
    }
}

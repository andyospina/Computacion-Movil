package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.time.TimeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Ventana del mapa: solo reseñas de las últimas 24 horas, para no saturarlo. */
const val RECENT_WINDOW_MILLIS = 24L * 60 * 60 * 1000

/** Tope de documentos que escucha el mapa. */
const val MAX_MAP_REVIEWS = 200

private const val REFRESH_INTERVAL_MILLIS = 60_000L

/**
 * Reseñas con ubicación publicadas en las últimas 24 horas (requisito del mapa).
 *
 * La ventana es móvil: la consulta trae desde «ahora − 24 h» y, además, cada minuto se vuelve a filtrar
 * por antigüedad, así una reseña sale del mapa cuando cumple 24 h aunque la pantalla siga abierta.
 */
class RecentReviewsRepository @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource,
    private val currentUserId: CurrentUserIdProvider,
    private val time: TimeProvider
) {

    fun observeRecentLocatedReviews(): Flow<Result<List<Review>>> {
        val uid = currentUserId.currentUserId()
        val since = time.nowMillis() - RECENT_WINDOW_MILLIS
        return remoteDataSource.observeReviewsSince(since, MAX_MAP_REVIEWS)
            .map { list -> list.map { it.toReview(uid) }.filter { it.location != null } }
            .combine(ticker()) { reviews, _ -> reviews.filter { it.isRecent(time.nowMillis()) } }
            .asResult()
    }

    private fun ticker(): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(REFRESH_INTERVAL_MILLIS)
        }
    }
}

/** Una reseña recién creada aún sin fecha del servidor (createdAtMillis nulo) cuenta como reciente. */
internal fun Review.isRecent(nowMillis: Long): Boolean =
    (createdAtMillis ?: Long.MAX_VALUE) >= nowMillis - RECENT_WINDOW_MILLIS

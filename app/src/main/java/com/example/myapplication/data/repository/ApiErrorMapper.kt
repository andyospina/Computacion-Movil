package com.example.myapplication.data.repository

import android.util.Log
import androidx.annotation.StringRes
import com.example.myapplication.R
import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.Result
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "Api"

@StringRes
fun Throwable.toApiErrorMessageRes(): Int = when (this) {
    is HttpException -> when (code()) {
        400 -> R.string.error_api_bad_request
        404 -> R.string.error_api_not_found
        in 500..599 -> R.string.error_api_server
        else -> R.string.error_api_unexpected
    }
    is NotFoundException -> R.string.error_api_not_found
    is NotAuthenticatedException -> R.string.error_session_required
    is UnsupportedOperationException -> R.string.error_not_supported_backend
    is FirebaseFirestoreException -> when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED,
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> R.string.error_permission_denied
        FirebaseFirestoreException.Code.NOT_FOUND -> R.string.error_api_not_found
        FirebaseFirestoreException.Code.UNAVAILABLE,
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> R.string.error_api_unreachable
        else -> R.string.error_api_unexpected
    }
    is IOException -> R.string.error_api_unreachable
    is SerializationException -> R.string.error_api_unexpected
    else -> R.string.error_api_unexpected
}

/** Ejecuta una llamada al backend y la convierte en Result; el detalle técnico solo va al log. */
suspend fun <T> runCatchingApi(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val detail = if (e is HttpException) "HTTP ${e.code()}" else e.javaClass.simpleName
        Log.w(TAG, "Llamada fallida ($detail): ${e.message}")
        Result.Failure(e, e.toApiErrorMessageRes())
    }
}

/**
 * Versión para datos en tiempo real: cada emisión es un Success y un error del listener se emite como
 * Failure (y termina el flujo; la pantalla puede volver a suscribirse con "Reintentar").
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    map<T, Result<T>> { Result.Success(it) }
        .catch { e ->
            Log.w(TAG, "Escucha fallida (${e.javaClass.simpleName}): ${e.message}")
            emit(Result.Failure(e, e.toApiErrorMessageRes()))
        }

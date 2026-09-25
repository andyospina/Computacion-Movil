package com.example.myapplication.data.repository

import androidx.annotation.StringRes
import com.example.myapplication.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.StorageException

@StringRes
fun Throwable.toStorageErrorMessageRes(): Int = when (this) {
    is NotAuthenticatedException -> R.string.error_not_authenticated
    is FirebaseNetworkException -> R.string.error_network
    is StorageException -> when (errorCode) {
        StorageException.ERROR_NOT_AUTHENTICATED -> R.string.error_not_authenticated
        StorageException.ERROR_NOT_AUTHORIZED -> R.string.error_storage_not_authorized
        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> R.string.error_network
        StorageException.ERROR_QUOTA_EXCEEDED -> R.string.error_storage_quota_exceeded
        else -> R.string.error_image_upload_failed
    }
    else -> R.string.error_image_upload_failed
}

class NotAuthenticatedException : IllegalStateException("No hay usuario autenticado")

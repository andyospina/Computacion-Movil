package com.example.myapplication.data

import androidx.annotation.StringRes

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(
        val exception: Throwable,
        @param:StringRes val messageRes: Int
    ) : Result<Nothing>()
}

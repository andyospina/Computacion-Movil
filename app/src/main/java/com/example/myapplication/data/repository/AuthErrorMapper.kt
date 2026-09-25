package com.example.myapplication.data.repository

import androidx.annotation.StringRes
import com.example.myapplication.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

@StringRes
fun Throwable.toAuthErrorMessageRes(): Int = when (this) {
    is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
    is FirebaseAuthInvalidCredentialsException -> R.string.error_invalid_credentials
    is FirebaseAuthUserCollisionException -> R.string.error_email_already_registered
    is FirebaseNetworkException -> R.string.error_network
    else -> R.string.error_authentication_failed
}

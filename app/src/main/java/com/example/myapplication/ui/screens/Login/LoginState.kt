package com.example.myapplication.ui.screens.Login

import androidx.annotation.StringRes
import com.example.myapplication.R

data class LoginState(
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    @param:StringRes val errorMessageRes: Int = R.string.error_all_fields_required,
    val showError: Boolean = false,
    val navigate: Boolean = false
)

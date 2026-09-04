package com.example.myapplication.ui.screens.Register

import androidx.annotation.StringRes
import com.example.myapplication.R

data class RegisterState(
    val email: String = "",
    val cellphone: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    @param:StringRes val errorMessageRes: Int = R.string.error_all_fields_required,
    val showError: Boolean = false,
    val navigate: Boolean = false
)

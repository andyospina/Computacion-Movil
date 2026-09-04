package com.example.myapplication.ui.screens.Register

data class RegisterState(
    val email: String = "",
    val cellphone: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val errorMessage: String = "",
    val showError: Boolean = false,
    val navigate: Boolean = false
)

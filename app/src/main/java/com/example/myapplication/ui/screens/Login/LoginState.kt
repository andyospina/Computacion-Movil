package com.example.myapplication.ui.screens.Login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val errorMessage: String = "",
    val showError: Boolean = false,
    val navigate: Boolean = false
)

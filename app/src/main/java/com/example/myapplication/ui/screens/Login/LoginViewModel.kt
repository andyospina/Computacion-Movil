package com.example.myapplication.ui.screens.Login

import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, showError = false) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, showError = false) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun loginButtonPress() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(showError = true, errorMessageRes = R.string.error_all_fields_required)
            }
            return
        }

        _uiState.update { it.copy(showError = false, navigate = true) }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigate = false) }
    }
}

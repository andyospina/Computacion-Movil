package com.example.myapplication.ui.screens.Register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val EMAIL_YA_REGISTRADO = "admin@admin.com"
private const val LONGITUD_MINIMA_PASSWORD = 6

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, showError = false) }
    }

    fun updateCellphone(value: String) {
        _uiState.update { it.copy(cellphone = value, showError = false) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, showError = false) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun registerButtonPress() {
        val state = _uiState.value

        when {
            state.email.isBlank() || state.cellphone.isBlank() || state.password.isBlank() -> {
                showError(R.string.error_all_fields_required)
            }

            state.password.length < LONGITUD_MINIMA_PASSWORD -> {
                showError(R.string.error_password_too_short)
            }

            state.email.equals(EMAIL_YA_REGISTRADO, ignoreCase = true) -> {
                showError(R.string.error_email_already_registered)
            }

            else -> {
                _uiState.update { it.copy(showError = false, navigate = true) }
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigate = false) }
    }

    private fun showError(@StringRes mensajeRes: Int) {
        _uiState.update { it.copy(showError = true, errorMessageRes = mensajeRes) }
    }
}

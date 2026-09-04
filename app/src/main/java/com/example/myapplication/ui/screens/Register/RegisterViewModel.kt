package com.example.myapplication.ui.screens.Register

import androidx.lifecycle.ViewModel
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
                showError("Todos los campos son obligatorios")
            }

            state.password.length < LONGITUD_MINIMA_PASSWORD -> {
                showError("La contraseña debe tener al menos 6 caracteres")
            }

            state.email.equals(EMAIL_YA_REGISTRADO, ignoreCase = true) -> {
                showError("Este correo ya está en uso")
            }

            else -> {
                _uiState.update { it.copy(showError = false, errorMessage = "", navigate = true) }
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigate = false) }
    }

    private fun showError(mensaje: String) {
        _uiState.update { it.copy(showError = true, errorMessage = mensaje) }
    }
}

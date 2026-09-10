package com.example.myapplication.ui.screens.Register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.toAuthErrorMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LONGITUD_MINIMA_PASSWORD = 6

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

            else -> signUp(state.email, state.password)
        }
    }

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false) }

            try {
                authRepository.signUp(email, password)
                _uiState.update { it.copy(isLoading = false, navigate = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showError = true,
                        errorMessageRes = e.toAuthErrorMessageRes()
                    )
                }
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

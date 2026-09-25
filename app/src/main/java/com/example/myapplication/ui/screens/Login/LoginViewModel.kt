package com.example.myapplication.ui.screens.Login

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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false) }

            try {
                authRepository.signIn(state.email, state.password)
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
}

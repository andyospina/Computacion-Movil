package com.example.myapplication.ui.screens.Register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LONGITUD_MINIMA_PASSWORD = 6

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, showError = false) }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, showError = false) }
    }

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value, showError = false) }
    }

    fun updateCountry(value: String) {
        _uiState.update { it.copy(country = value, showError = false) }
    }

    fun updateBio(value: String) {
        _uiState.update { it.copy(bio = value, showError = false) }
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
            state.email.isBlank() || state.cellphone.isBlank() || state.password.isBlank() ||
                state.name.isBlank() || state.username.isBlank() -> {
                showError(R.string.error_all_fields_required)
            }

            state.password.length < LONGITUD_MINIMA_PASSWORD -> {
                showError(R.string.error_password_too_short)
            }

            else -> signUp(state)
        }
    }

    // Auth crea la cuenta y después se guarda su perfil en users/{uid}. Si el perfil falla se
    // revierte la cuenta para que el usuario pueda reintentar con el mismo correo.
    private fun signUp(state: RegisterState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false) }

            val signUpResult = authRepository.signUp(state.email, state.password)
            if (signUpResult is Result.Failure) return@launch fail(signUpResult.messageRes)

            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                authRepository.signOut()
                return@launch fail(R.string.error_authentication_failed)
            }

            val profileResult = userRepository.registerUser(
                userId = uid,
                username = state.username,
                name = state.name,
                email = state.email,
                country = state.country,
                bio = state.bio
            )
            when (profileResult) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, navigate = true) }
                is Result.Failure -> {
                    if (authRepository.deleteCurrentUser() is Result.Failure) authRepository.signOut()
                    fail(profileResult.messageRes)
                }
            }
        }
    }

    private fun fail(@StringRes mensajeRes: Int) {
        _uiState.update { it.copy(isLoading = false, showError = true, errorMessageRes = mensajeRes) }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigate = false) }
    }

    private fun showError(@StringRes mensajeRes: Int) {
        _uiState.update { it.copy(showError = true, errorMessageRes = mensajeRes) }
    }
}

package com.example.myapplication.ui.screens.Profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        getPerfil()
        getProfileImage()
    }

    private fun getPerfil() {
        _uiState.update {
            it.copy(
                user = LocalUserProvider.currentUser,
                misResenas = LocalReviewProvider.byUser("Emily"),
                email = authRepository.currentUser?.email.orEmpty(),
                profileImageUrl = authRepository.currentUser?.photoUrl?.toString()
            )
        }
    }

    // Refresca la photoUrl desde Firebase por si se cambió desde otro dispositivo.
    // Si falla (p. ej. sin conexión) se conserva la URL en caché.
    private fun getProfileImage() {
        viewModelScope.launch {
            val result = authRepository.getProfileImageUrl()
            if (result is Result.Success && !_uiState.value.isLoading) {
                _uiState.update { it.copy(profileImageUrl = result.data) }
            }
        }
    }

    fun onImageSelected(imageUri: Uri?) {
        if (imageUri == null) return

        val urlAnterior = _uiState.value.profileImageUrl

        // La imagen local se muestra de inmediato, antes de terminar la subida
        _uiState.update {
            it.copy(profileImageUrl = imageUri.toString(), isLoading = true, errorMessageRes = null)
        }

        viewModelScope.launch {
            when (val result = storageRepository.uploadProfileImage(imageUri)) {
                is Result.Success -> _uiState.update {
                    it.copy(profileImageUrl = result.data, isLoading = false)
                }
                is Result.Failure -> _uiState.update {
                    it.copy(
                        profileImageUrl = urlAnterior,
                        isLoading = false,
                        errorMessageRes = result.messageRes
                    )
                }
            }
        }
    }

    fun logoutButtonPress() {
        authRepository.signOut()
        _uiState.update { it.copy(loggedOut = true) }
    }

    fun onLoggedOut() {
        _uiState.update { it.copy(loggedOut = false) }
    }
}

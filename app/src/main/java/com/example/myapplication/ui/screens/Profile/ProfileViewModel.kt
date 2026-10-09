package com.example.myapplication.ui.screens.Profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ApiConfig
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.StorageRepository
import com.example.myapplication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileState(
            email = authRepository.currentUser?.email.orEmpty(),
            profileImageUrl = authRepository.currentUser?.photoUrl?.toString()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        getProfileImage()
    }

    // Se llama al entrar a la pantalla y al volver de editar una reseña.
    fun getPerfil() {
        // Con datos ya cargados se refresca sin mostrar el indicador de carga
        _uiState.update {
            it.copy(isLoadingData = it.user == null, dataErrorRes = null)
        }

        viewModelScope.launch {
            val usuario = when (val result = userRepository.getUserById(ApiConfig.TEMP_CURRENT_USER_ID)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }
            val resenas = when (val result = reviewRepository.getReviewsByUser(ApiConfig.TEMP_CURRENT_USER_ID)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }

            _uiState.update {
                it.copy(
                    user = usuario.conEstadisticas(resenas),
                    misResenas = resenas,
                    isLoadingData = false,
                    dataErrorRes = null
                )
            }
        }
    }

    fun deleteResena(reviewId: String) {
        _uiState.update { it.copy(errorMessageRes = null) }

        viewModelScope.launch {
            when (val result = reviewRepository.deleteReview(reviewId)) {
                // Se quita de la lista local sin volver a pedirla al backend
                is Result.Success -> _uiState.update { state ->
                    val resenas = state.misResenas.filter { it.id != reviewId }
                    state.copy(misResenas = resenas, user = state.user?.conEstadisticas(resenas))
                }
                is Result.Failure -> _uiState.update { it.copy(errorMessageRes = result.messageRes) }
            }
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update { it.copy(isLoadingData = false, dataErrorRes = messageRes) }
    }

    private fun User.conEstadisticas(resenas: List<Review>): User = copy(
        reviewCount = resenas.size,
        averageRating = if (resenas.isEmpty()) 0.0
        else Math.round(resenas.map { it.rating }.average() * 10) / 10.0
    )

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

package com.example.myapplication.ui.screens.Profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.Result
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.NotificationRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.StorageRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.data.withReviewStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val reviewRepository: ReviewRepository,
    private val followRepository: FollowRepository,
    private val notificationRepository: NotificationRepository,
    private val currentUserId: CurrentUserIdProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileState(
            email = authRepository.currentUser?.email.orEmpty(),
            profileImageUrl = authRepository.currentUser?.photoUrl?.toString()
        )
    )
    val uiState = _uiState.asStateFlow()

    private var seguidoresJob: Job? = null

    init {
        getProfileImage()
    }

    // Se llama al entrar a la pantalla y al volver de editar una reseña.
    fun getPerfil() {
        // Con datos ya cargados se refresca sin mostrar el indicador de carga
        _uiState.update {
            it.copy(isLoadingData = it.user == null, dataErrorRes = null)
        }

        // El perfil propio se consulta con el id de la sesión actual, nunca con un id fijo
        val uid = currentUserId.currentUserId()
        if (uid == null) {
            falla(R.string.error_not_authenticated_profile)
            return
        }
        _uiState.update { it.copy(userId = uid) }
        observarSeguidores(uid)

        viewModelScope.launch {
            val usuario = when (val result = userRepository.getUserById(uid)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }
            val resenas = when (val result = reviewRepository.getReviewsByUser(uid)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }

            _uiState.update {
                it.copy(
                    user = usuario.withReviewStats(resenas),
                    misResenas = resenas,
                    isLoadingData = false,
                    dataErrorRes = null
                )
            }
        }
    }

    fun updateProfile(name: String, country: String, bio: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(profileSaveErrorRes = R.string.error_name_required) }
            return
        }
        val uid = currentUserId.currentUserId() ?: return
        _uiState.update { it.copy(isSavingProfile = true, profileSaveErrorRes = null) }

        viewModelScope.launch {
            when (val result = userRepository.updateUser(uid, name, country, bio)) {
                // Se actualiza la copia local solo después de confirmar el guardado
                is Result.Success -> _uiState.update {
                    it.copy(
                        user = it.user?.copy(name = name.trim(), country = country.trim(), bio = bio.trim()),
                        isSavingProfile = false,
                        profileSaved = true
                    )
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isSavingProfile = false, profileSaveErrorRes = result.messageRes)
                }
            }
        }
    }

    fun onProfileSaved() {
        _uiState.update { it.copy(profileSaved = false) }
    }

    fun onEditDismissed() {
        _uiState.update { it.copy(profileSaveErrorRes = null) }
    }

    fun deleteResena(reviewId: String) {
        _uiState.update { it.copy(errorMessageRes = null) }

        viewModelScope.launch {
            when (val result = reviewRepository.deleteReview(reviewId)) {
                // Se quita de la lista local sin volver a pedirla al backend
                is Result.Success -> _uiState.update { state ->
                    val resenas = state.misResenas.filter { it.id != reviewId }
                    state.copy(misResenas = resenas, user = state.user?.withReviewStats(resenas))
                }
                is Result.Failure -> _uiState.update { it.copy(errorMessageRes = result.messageRes) }
            }
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update { it.copy(isLoadingData = false, dataErrorRes = messageRes) }
    }

    // Contadores de seguidores y seguidos en tiempo real
    private fun observarSeguidores(uid: String) {
        if (seguidoresJob?.isActive == true) return
        seguidoresJob = viewModelScope.launch {
            followRepository.observeFollowStats(uid).collect { result ->
                if (result is Result.Success) _uiState.update { it.copy(followStats = result.data) }
            }
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
                is Result.Success -> {
                    _uiState.update { it.copy(profileImageUrl = result.data) }
                    // Copia la URL al documento del usuario para que otros vean su foto
                    val uid = currentUserId.currentUserId()
                    val sync = if (uid == null) null else userRepository.updateProfileImage(uid, result.data)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = (sync as? Result.Failure)?.messageRes
                        )
                    }
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

    // Primero se borra el token FCM (requiere la sesión activa) para que este dispositivo no reciba
    // notificaciones de esta cuenta; si falla igual se cierra la sesión.
    fun logoutButtonPress() {
        viewModelScope.launch {
            notificationRepository.unregisterDevice()
            authRepository.signOut()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }

    fun onLoggedOut() {
        _uiState.update { it.copy(loggedOut = false) }
    }
}

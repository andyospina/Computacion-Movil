package com.example.myapplication.ui.screens.UserProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.data.withReviewStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Perfil de otro usuario: sus datos, seguidores en vivo, botón seguir y sus reseñas (con likes). */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val followRepository: FollowRepository,
    private val currentUserId: CurrentUserIdProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState = _uiState.asStateFlow()

    private var observacionJob: Job? = null

    fun getPerfil(userId: String) {
        val state = _uiState.value
        if (state.userId != userId || state.user == null) cargar(userId)
        observar(userId)
    }

    fun reintentar() = getPerfil(_uiState.value.userId)

    fun toggleSeguir() {
        val state = _uiState.value
        if (state.esMiPerfil || state.isFollowLoading) return
        _uiState.update { it.copy(isFollowLoading = true) }

        viewModelScope.launch {
            // El listener de seguidores actualiza el contador y el botón
            val result = if (state.siguiendo) followRepository.unfollow(state.userId)
            else followRepository.follow(state.userId)
            _uiState.update {
                it.copy(isFollowLoading = false, actionErrorRes = (result as? Result.Failure)?.messageRes)
            }
        }
    }

    fun toggleLike(resena: Review) {
        viewModelScope.launch {
            val result = reviewRepository.toggleLike(resena)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun cargar(userId: String) {
        _uiState.update {
            it.copy(
                userId = userId,
                esMiPerfil = userId == currentUserId.currentUserId(),
                isLoading = true,
                errorMessageRes = null
            )
        }

        viewModelScope.launch {
            when (val result = userRepository.getUserById(userId)) {
                is Result.Success -> _uiState.update {
                    it.copy(user = result.data.withReviewStats(it.resenas), isLoading = false)
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    // Reseñas y seguidores en tiempo real. Si ya se escuchan no se abren listeners nuevos.
    private fun observar(userId: String) {
        if (observacionJob?.isActive == true) return
        observacionJob = viewModelScope.launch {
            launch {
                reviewRepository.observeReviewsByUsers(listOf(userId)).collect { result ->
                    when (result) {
                        is Result.Success -> _uiState.update {
                            it.copy(resenas = result.data, user = it.user?.withReviewStats(result.data))
                        }
                        is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
                    }
                }
            }
            launch {
                followRepository.observeFollowStats(userId).collect { result ->
                    when (result) {
                        is Result.Success -> _uiState.update {
                            it.copy(
                                followStats = result.data,
                                siguiendo = result.data.isFollowedBy(currentUserId.currentUserId())
                            )
                        }
                        is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
                    }
                }
            }
        }
    }
}

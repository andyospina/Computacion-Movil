package com.example.myapplication.ui.screens.UserProfile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileState(
    val userId: String = "",
    val user: User? = null,
    val resenas: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null
)

/** Perfil de otro usuario (solo lectura): sus datos y las reseñas que ha escrito. */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState = _uiState.asStateFlow()

    fun getPerfil(userId: String) {
        val state = _uiState.value
        if (state.userId == userId && state.user != null) return
        cargar(userId)
    }

    fun reintentar() = cargar(_uiState.value.userId)

    private fun cargar(userId: String) {
        _uiState.update { it.copy(userId = userId, isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            val usuario = when (val result = userRepository.getUserById(userId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }
            val resenas = when (val result = reviewRepository.getReviewsByUser(userId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }

            _uiState.update {
                it.copy(
                    user = usuario.copy(
                        reviewCount = resenas.size,
                        averageRating = if (resenas.isEmpty()) 0.0
                        else Math.round(resenas.map { r -> r.rating }.average() * 10) / 10.0
                    ),
                    resenas = resenas,
                    isLoading = false,
                    errorMessageRes = null
                )
            }
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update { it.copy(isLoading = false, errorMessageRes = messageRes) }
    }
}

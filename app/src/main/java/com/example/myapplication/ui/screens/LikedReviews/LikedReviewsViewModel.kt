package com.example.myapplication.ui.screens.LikedReviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** «Reseñas que me gustaron»: la lista cambia en vivo cuando se da o se quita un like desde cualquier pantalla. */
@HiltViewModel
class LikedReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LikedReviewsState())
    val uiState = _uiState.asStateFlow()

    private var observacionJob: Job? = null

    init {
        observar()
    }

    fun reintentar() {
        observacionJob?.cancel()
        observar()
    }

    /** Quitar el like saca la reseña de la lista: el listener emite la lista sin ella. */
    fun toggleLike(resena: Review) {
        viewModelScope.launch {
            val result = reviewRepository.toggleLike(resena)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun observar() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        observacionJob = viewModelScope.launch {
            reviewRepository.observeMyLikedReviews().collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(resenas = result.data, isLoading = false, errorMessageRes = null)
                    }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = result.messageRes)
                    }
                }
            }
        }
    }
}

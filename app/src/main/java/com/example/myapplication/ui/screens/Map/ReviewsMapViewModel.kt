package com.example.myapplication.ui.screens.Map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.RecentReviewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Mapa de reseñas publicadas en las últimas 24 h; se actualiza en vivo y sin pedir nada al usuario. */
@HiltViewModel
class ReviewsMapViewModel @Inject constructor(
    private val recentReviews: RecentReviewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsMapState())
    val uiState = _uiState.asStateFlow()

    private var observacionJob: Job? = null

    init {
        observar()
    }

    fun reintentar() {
        observacionJob?.cancel()
        observar()
    }

    fun onMarcadorClick(reviewId: String) {
        _uiState.update { it.copy(selectedReviewId = reviewId) }
    }

    fun onSeleccionCerrada() {
        _uiState.update { it.copy(selectedReviewId = null) }
    }

    private fun observar() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        observacionJob = viewModelScope.launch {
            recentReviews.observeRecentLocatedReviews().collect { result ->
                when (result) {
                    // Si la reseña seleccionada sale de la lista (cumplió 24 h o la borraron), la tarjeta se cierra.
                    is Result.Success -> _uiState.update { state ->
                        state.copy(
                            reviews = result.data,
                            selectedReviewId = state.selectedReviewId?.takeIf { id -> result.data.any { it.id == id } },
                            isLoading = false,
                            errorMessageRes = null
                        )
                    }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = result.messageRes)
                    }
                }
            }
        }
    }
}

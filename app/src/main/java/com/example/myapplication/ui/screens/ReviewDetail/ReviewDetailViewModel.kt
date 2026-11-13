package com.example.myapplication.ui.screens.ReviewDetail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewDetailState(
    val review: Review? = null,
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

/** Detalle de una reseña (destino al tocar «Ver detalle» en el mapa). */
@HiltViewModel
class ReviewDetailViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewDetailState())
    val uiState = _uiState.asStateFlow()

    private var reviewId: String = ""

    fun cargar(id: String) {
        reviewId = id
        _uiState.update { it.copy(isLoading = it.review == null, errorMessageRes = null) }
        viewModelScope.launch {
            when (val result = reviewRepository.getReviewById(id)) {
                is Result.Success -> _uiState.update {
                    it.copy(review = result.data, isLoading = false, errorMessageRes = null)
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = if (it.review == null) result.messageRes else null,
                        actionErrorRes = if (it.review != null) result.messageRes else null)
                }
            }
        }
    }

    fun reintentar() = cargar(reviewId)

    fun toggleLike() {
        val review = _uiState.value.review ?: return
        viewModelScope.launch {
            when (val result = reviewRepository.toggleLike(review)) {
                is Result.Success -> cargar(review.id)
                is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
            }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }
}

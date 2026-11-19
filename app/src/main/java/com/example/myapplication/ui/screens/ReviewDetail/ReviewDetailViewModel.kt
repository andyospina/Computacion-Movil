package com.example.myapplication.ui.screens.ReviewDetail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.ReportReason
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.ReportRepository
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
    @param:StringRes val actionErrorRes: Int? = null,
    // Reportar: solo se ofrece en reseñas ajenas y con sesión iniciada
    val canReport: Boolean = false,
    val alreadyReported: Boolean = false,
    val showReportDialog: Boolean = false,
    val isReporting: Boolean = false,
    @param:StringRes val infoMessageRes: Int? = null
)

/** Detalle de una reseña (destino al tocar «Ver detalle» en el mapa). */
@HiltViewModel
class ReviewDetailViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val reportRepository: ReportRepository,
    private val currentUserId: CurrentUserIdProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewDetailState())
    val uiState = _uiState.asStateFlow()

    private var reviewId: String = ""

    fun cargar(id: String) {
        reviewId = id
        _uiState.update { it.copy(isLoading = it.review == null, errorMessageRes = null) }
        viewModelScope.launch {
            when (val result = reviewRepository.getReviewById(id)) {
                is Result.Success -> {
                    val me = currentUserId.currentUserId()
                    _uiState.update {
                        it.copy(
                            review = result.data,
                            isLoading = false,
                            errorMessageRes = null,
                            canReport = me != null && result.data.userId != me
                        )
                    }
                    if (_uiState.value.canReport) cargarEstadoReporte(result.data.id)
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = if (it.review == null) result.messageRes else null,
                        actionErrorRes = if (it.review != null) result.messageRes else null)
                }
            }
        }
    }

    // Si falla la consulta simplemente se ofrece reportar; el reporte repetido no es un error
    private suspend fun cargarEstadoReporte(id: String) {
        val result = reportRepository.hasReported(id)
        if (result is Result.Success) _uiState.update { it.copy(alreadyReported = result.data) }
    }

    fun onReportClick() {
        val state = _uiState.value
        if (state.canReport && !state.alreadyReported) _uiState.update { it.copy(showReportDialog = true) }
    }

    fun onReportDismissed() {
        if (!_uiState.value.isReporting) _uiState.update { it.copy(showReportDialog = false) }
    }

    fun enviarReporte(reason: ReportReason) {
        val review = _uiState.value.review ?: return
        if (_uiState.value.isReporting) return
        _uiState.update { it.copy(isReporting = true) }
        viewModelScope.launch {
            when (val result = reportRepository.report(review.id, reason)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isReporting = false,
                        showReportDialog = false,
                        alreadyReported = true,
                        infoMessageRes = R.string.report_sent
                    )
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isReporting = false, actionErrorRes = result.messageRes)
                }
            }
        }
    }

    fun onInfoMessageShown() {
        _uiState.update { it.copy(infoMessageRes = null) }
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

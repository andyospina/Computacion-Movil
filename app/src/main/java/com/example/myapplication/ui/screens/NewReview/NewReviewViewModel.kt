package com.example.myapplication.ui.screens.NewReview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.ApiConfig
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val PUNTUACIONES = 1..5

@HiltViewModel
class NewReviewViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewReviewState())
    val uiState = _uiState.asStateFlow()

    private var inicializado = false

    /**
     * [reviewId] vacío = crear una reseña; con valor = editar esa reseña y precargar sus datos.
     * [productId] vacío (flujo desde el hub) = se usa el primer producto del catálogo.
     */
    fun inicializar(productId: String, reviewId: String) {
        if (inicializado) return
        inicializado = true

        _uiState.update {
            it.copy(productId = productId, reviewId = reviewId.ifBlank { null })
        }

        when {
            reviewId.isNotBlank() -> cargarResena(reviewId)
            productId.isBlank() -> elegirProductoPorDefecto()
        }
    }

    private fun cargarResena(reviewId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            when (val result = reviewRepository.getReviewById(reviewId)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        productId = result.data.productId,
                        puntuacion = result.data.rating,
                        titulo = result.data.title,
                        resena = result.data.body,
                        isLoading = false
                    )
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    private fun elegirProductoPorDefecto() {
        viewModelScope.launch {
            when (val result = productRepository.getProducts()) {
                is Result.Success -> _uiState.update {
                    it.copy(productId = result.data.firstOrNull()?.id.orEmpty())
                }
                is Result.Failure -> _uiState.update { it.copy(errorMessageRes = result.messageRes) }
            }
        }
    }

    fun updatePuntuacion(value: Int) {
        _uiState.update { it.copy(puntuacion = value) }
    }

    fun updateTitulo(value: String) {
        _uiState.update { it.copy(titulo = value) }
    }

    fun updateResena(value: String) {
        _uiState.update { it.copy(resena = value) }
    }

    fun updateUsuario(value: String) {
        _uiState.update { it.copy(usuario = value) }
    }

    fun updateCorreo(value: String) {
        _uiState.update { it.copy(correo = value) }
    }

    fun publicarButtonPress() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.puntuacion !in PUNTUACIONES || state.titulo.isBlank() || state.resena.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.new_review_missing_fields) }
            return
        }
        if (state.productId.isBlank() && !state.esEdicion) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_api_unexpected) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            val result = if (state.reviewId != null) {
                reviewRepository.updateReview(state.reviewId, state.puntuacion, state.titulo, state.resena)
            } else {
                reviewRepository.createReview(
                    productId = state.productId,
                    userId = ApiConfig.TEMP_CURRENT_USER_ID,
                    rating = state.puntuacion,
                    title = state.titulo,
                    body = state.resena
                )
            }

            // En error se conserva el formulario para poder reintentar
            when (result) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, publicado = true) }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    fun onPublicado() {
        _uiState.update { it.copy(publicado = false) }
    }
}

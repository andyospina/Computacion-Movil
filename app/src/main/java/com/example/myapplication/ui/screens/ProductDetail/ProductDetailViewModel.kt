package com.example.myapplication.ui.screens.ProductDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FEATURED_REVIEWS = 2

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val savedRepository: SavedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailState())
    val uiState = _uiState.asStateFlow()

    private var resenasJob: Job? = null
    private var guardadoJob: Job? = null

    // Se llama al entrar y al volver (p. ej. tras escribir una reseña): refresca sin parpadeo.
    fun getProducto(productId: String) {
        cargar(productId)
        observarResenas(productId)
        observarGuardado(productId)
    }

    fun reintentar() = getProducto(_uiState.value.productId)

    fun toggleLike(resena: Review) {
        viewModelScope.launch {
            // No hace falta actualizar la lista: el listener de reseñas emite el cambio
            val result = reviewRepository.toggleLike(resena)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    /** El botón cambia cuando el listener confirma el guardado, así nunca miente sobre el estado real. */
    fun toggleGuardado() {
        val state = _uiState.value
        if (state.productId.isBlank()) return
        viewModelScope.launch {
            val result = savedRepository.setSaved(state.productId, saved = !state.guardado)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun cargar(productId: String) {
        _uiState.update {
            it.copy(productId = productId, isLoading = it.producto == null, errorMessageRes = null)
        }

        viewModelScope.launch {
            when (val result = productRepository.getProductById(productId)) {
                is Result.Success -> _uiState.update {
                    it.copy(producto = result.data, isLoading = false, errorMessageRes = null)
                }
                is Result.Failure -> falla(result.messageRes)
            }
        }
    }

    // Reseñas en tiempo real: likes y reseñas nuevas aparecen sin recargar. Si ya se escucha este
    // producto no se abre un segundo listener.
    private fun observarResenas(productId: String) {
        if (resenasJob?.isActive == true) return
        resenasJob = viewModelScope.launch {
            reviewRepository.observeReviewsByProduct(productId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(resenasDestacadas = result.data.take(FEATURED_REVIEWS))
                    }
                    is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
                }
            }
        }
    }

    private fun observarGuardado(productId: String) {
        if (guardadoJob?.isActive == true) return
        guardadoJob = viewModelScope.launch {
            savedRepository.observeIsSaved(productId).collect { result ->
                if (result is Result.Success) _uiState.update { it.copy(guardado = result.data) }
            }
        }
    }

    private fun falla(messageRes: Int) {
        // Si ya había datos de una carga anterior se conservan en pantalla
        _uiState.update {
            it.copy(isLoading = false, errorMessageRes = if (it.producto == null) messageRes else null)
        }
    }
}

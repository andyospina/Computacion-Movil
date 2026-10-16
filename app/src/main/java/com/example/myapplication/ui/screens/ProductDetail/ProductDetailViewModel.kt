package com.example.myapplication.ui.screens.ProductDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FEATURED_REVIEWS = 2

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailState())
    val uiState = _uiState.asStateFlow()

    // Se llama al entrar y al volver (p. ej. tras escribir una reseña): refresca sin parpadeo.
    fun getProducto(productId: String) = cargar(productId)

    fun reintentar() = cargar(_uiState.value.productId)

    private fun cargar(productId: String) {
        _uiState.update {
            it.copy(productId = productId, isLoading = it.producto == null, errorMessageRes = null)
        }

        viewModelScope.launch {
            val producto = when (val result = productRepository.getProductById(productId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }
            val resenas = when (val result = reviewRepository.getReviewsByProduct(productId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }

            _uiState.update {
                it.copy(
                    producto = producto,
                    resenasDestacadas = resenas.take(FEATURED_REVIEWS),
                    isLoading = false,
                    errorMessageRes = null
                )
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

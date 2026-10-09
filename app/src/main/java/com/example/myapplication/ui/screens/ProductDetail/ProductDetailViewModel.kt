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

    fun getProducto(productId: String) {
        val state = _uiState.value
        if (state.productId == productId && state.producto != null) return
        cargar(productId)
    }

    fun reintentar() = cargar(_uiState.value.productId)

    private fun cargar(productId: String) {
        _uiState.update { it.copy(productId = productId, isLoading = true, errorMessageRes = null) }

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
        _uiState.update { it.copy(isLoading = false, errorMessageRes = messageRes) }
    }
}

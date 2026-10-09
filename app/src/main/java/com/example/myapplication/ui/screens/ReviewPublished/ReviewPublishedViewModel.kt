package com.example.myapplication.ui.screens.ReviewPublished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewPublishedViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewPublishedState())
    val uiState = _uiState.asStateFlow()

    fun getProducto(productId: String) {
        if (_uiState.value.productId == productId) return

        _uiState.update { it.copy(productId = productId) }

        // Si falla, la pantalla no muestra la tarjeta del producto: la reseña ya se guardó.
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            if (result is Result.Success) {
                _uiState.update { it.copy(producto = result.data) }
            }
        }
    }
}

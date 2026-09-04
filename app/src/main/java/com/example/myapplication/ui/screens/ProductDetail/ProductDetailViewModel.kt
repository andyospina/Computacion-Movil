package com.example.myapplication.ui.screens.ProductDetail

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.LocalReviewProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailState())
    val uiState = _uiState.asStateFlow()

    fun getProducto(productId: String) {
        if (_uiState.value.productId == productId) return

        _uiState.update {
            it.copy(
                productId = productId,
                producto = LocalProductProvider.findById(productId),
                resenasDestacadas = LocalReviewProvider.featuredForProduct(productId)
            )
        }
    }
}

package com.example.myapplication.ui.screens.ProductDetail

import com.example.myapplication.data.Product
import com.example.myapplication.data.Review

data class ProductDetailState(
    val productId: String = "",
    val producto: Product? = null,
    val resenasDestacadas: List<Review> = emptyList()
)

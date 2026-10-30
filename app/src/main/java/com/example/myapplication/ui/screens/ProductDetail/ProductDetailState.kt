package com.example.myapplication.ui.screens.ProductDetail

import androidx.annotation.StringRes
import com.example.myapplication.data.Product
import com.example.myapplication.data.Review

data class ProductDetailState(
    val productId: String = "",
    val producto: Product? = null,
    val resenasDestacadas: List<Review> = emptyList(),
    val guardado: Boolean = false,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null,
    // Falla puntual (p. ej. un like) que no debe ocultar la pantalla
    @param:StringRes val actionErrorRes: Int? = null
)

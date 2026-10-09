package com.example.myapplication.ui.screens.Home

import androidx.annotation.StringRes
import com.example.myapplication.data.Product

data class HomeState(
    val categoriaSeleccionada: String = "Todo",
    val productos: List<Product> = emptyList(),
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null
)

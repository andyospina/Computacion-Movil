package com.example.myapplication.ui.screens.Home

import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.Product

data class HomeState(
    val categoriaSeleccionada: String = "Todo",
    val productos: List<Product> = LocalProductProvider.trending()
)

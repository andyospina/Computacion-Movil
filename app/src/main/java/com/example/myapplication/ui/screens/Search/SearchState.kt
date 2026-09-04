package com.example.myapplication.ui.screens.Search

import com.example.myapplication.data.Product

data class SearchState(
    val query: String = "",
    val resultados: List<Product> = emptyList()
)

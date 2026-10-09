package com.example.myapplication.ui.screens.Search

import androidx.annotation.StringRes
import com.example.myapplication.data.Product

data class SearchState(
    val query: String = "",
    val resultados: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null
)

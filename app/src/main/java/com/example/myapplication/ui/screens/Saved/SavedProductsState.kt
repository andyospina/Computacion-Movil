package com.example.myapplication.ui.screens.Saved

import androidx.annotation.StringRes
import com.example.myapplication.data.Product

data class SavedProductsState(
    val productos: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

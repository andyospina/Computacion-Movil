package com.example.myapplication.ui.screens.Home

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalProductProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
        getProductos(_uiState.value.categoriaSeleccionada)
    }

    fun updateCategoria(categoria: String) {
        getProductos(categoria)
    }

    private fun getProductos(categoria: String) {
        val productos = if (categoria == "Todo") {
            LocalProductProvider.trending()
        } else {
            LocalProductProvider.products.filter { it.category == categoria }
        }

        _uiState.update { it.copy(categoriaSeleccionada = categoria, productos = productos) }
    }
}

package com.example.myapplication.ui.screens.Reviews

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.ui.screens.Reviews.componentes.OrdenResenas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val PAGE_SIZE = 4

class ReviewsListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsListState())
    val uiState = _uiState.asStateFlow()

    fun getResenas(productId: String) {
        if (_uiState.value.productId == productId) return

        val todasLasResenas = LocalReviewProvider.forProduct(productId)

        _uiState.update {
            it.copy(
                productId = productId,
                producto = LocalProductProvider.findById(productId),
                todasLasResenas = todasLasResenas,
                resenasFiltradas = todasLasResenas,
                cantidadVisible = PAGE_SIZE
            )
        }
    }

    fun updateOrden(orden: OrdenResenas) {
        _uiState.update { it.copy(orden = orden) }
        aplicarFiltros()
    }

    fun updateConFotos(conFotos: Boolean) {
        _uiState.update { it.copy(conFotos = conFotos) }
        aplicarFiltros()
    }

    fun cargarMas() {
        _uiState.update { it.copy(cantidadVisible = it.cantidadVisible + PAGE_SIZE) }
    }

    private fun aplicarFiltros() {
        val state = _uiState.value

        val filtradas = state.todasLasResenas
            .let { lista -> if (state.conFotos) lista.filter { it.hasPhoto } else lista }
            .let { lista ->
                if (state.orden == OrdenResenas.MEJOR_VALORADAS) lista.sortedByDescending { it.rating }
                else lista
            }

        _uiState.update { it.copy(resenasFiltradas = filtradas, cantidadVisible = PAGE_SIZE) }
    }
}

package com.example.myapplication.ui.screens.Reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.ui.screens.Reviews.componentes.OrdenResenas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 4

@HiltViewModel
class ReviewsListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsListState())
    val uiState = _uiState.asStateFlow()

    fun getResenas(productId: String) {
        val state = _uiState.value
        if (state.productId == productId && state.producto != null) return
        cargar(productId)
    }

    fun reintentar() = cargar(_uiState.value.productId)

    private fun cargar(productId: String) {
        _uiState.update { it.copy(productId = productId, isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            val producto = when (val result = productRepository.getProductById(productId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }
            val resenas = when (val result = reviewRepository.getReviewsByProduct(productId)) {
                is Result.Success -> result.data
                is Result.Failure -> return@launch falla(result.messageRes)
            }

            _uiState.update {
                it.copy(
                    producto = producto,
                    todasLasResenas = resenas,
                    isLoading = false,
                    errorMessageRes = null
                )
            }
            aplicarFiltros()
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update { it.copy(isLoading = false, errorMessageRes = messageRes) }
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

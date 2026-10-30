package com.example.myapplication.ui.screens.Reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.ui.screens.Reviews.componentes.OrdenResenas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var resenasJob: Job? = null

    // Se llama al entrar y al volver a la pantalla: refresca el producto sin parpadeo.
    fun getResenas(productId: String) {
        cargarProducto(productId)
        observarResenas(productId)
    }

    fun reintentar() = getResenas(_uiState.value.productId)

    fun toggleLike(resena: Review) {
        viewModelScope.launch {
            val result = reviewRepository.toggleLike(resena)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun cargarProducto(productId: String) {
        _uiState.update {
            it.copy(productId = productId, isLoading = it.producto == null, errorMessageRes = null)
        }

        viewModelScope.launch {
            when (val result = productRepository.getProductById(productId)) {
                is Result.Success -> _uiState.update {
                    it.copy(producto = result.data, isLoading = false, errorMessageRes = null)
                }
                is Result.Failure -> falla(result.messageRes)
            }
        }
    }

    // Tiempo real: cada cambio (like, reseña nueva o editada) vuelve a aplicar los filtros sin
    // perder cuántas reseñas tenía desplegadas el usuario.
    private fun observarResenas(productId: String) {
        if (resenasJob?.isActive == true) return
        resenasJob = viewModelScope.launch {
            reviewRepository.observeReviewsByProduct(productId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(todasLasResenas = result.data) }
                        aplicarFiltros(reiniciarPaginacion = false)
                    }
                    is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
                }
            }
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update {
            it.copy(isLoading = false, errorMessageRes = if (it.producto == null) messageRes else null)
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

    private fun aplicarFiltros(reiniciarPaginacion: Boolean = true) {
        val state = _uiState.value

        val filtradas = state.todasLasResenas
            .let { lista -> if (state.conFotos) lista.filter { it.hasPhoto } else lista }
            .let { lista ->
                when (state.orden) {
                    OrdenResenas.MEJOR_VALORADAS -> lista.sortedByDescending { it.rating }
                    // sortedByDescending es estable: a igualdad de likes queda primero la más reciente
                    OrdenResenas.MAS_LIKES -> lista.sortedByDescending { it.likesCount }
                    OrdenResenas.RECIENTES -> lista
                }
            }

        _uiState.update {
            it.copy(
                resenasFiltradas = filtradas,
                cantidadVisible = if (reiniciarPaginacion) PAGE_SIZE else it.cantidadVisible
            )
        }
    }
}

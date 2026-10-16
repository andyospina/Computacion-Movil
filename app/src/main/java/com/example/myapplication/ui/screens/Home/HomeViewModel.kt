package com.example.myapplication.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CATEGORIA_TODO = "Todo"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private var todosLosProductos: List<Product> = emptyList()

    init {
        getProfileImage()
    }

    fun updateCategoria(categoria: String) {
        _uiState.update {
            it.copy(categoriaSeleccionada = categoria, productos = filtrar(categoria))
        }
    }

    fun getProfileImage() {
        _uiState.update { it.copy(profileImageUrl = authRepository.currentUser?.photoUrl?.toString()) }
    }

    // Se llama al entrar a la pantalla y al volver a ella (p. ej. tras publicar o borrar una reseña).
    // Con datos ya cargados se refresca sin indicador de carga y un fallo no oculta lo que ya se ve.
    fun getProductos() {
        _uiState.update { it.copy(isLoading = todosLosProductos.isEmpty(), errorMessageRes = null) }

        viewModelScope.launch {
            when (val result = productRepository.getProducts()) {
                is Result.Success -> {
                    todosLosProductos = result.data
                    _uiState.update {
                        it.copy(
                            productos = filtrar(it.categoriaSeleccionada),
                            isLoading = false,
                            errorMessageRes = null
                        )
                    }
                }
                is Result.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = if (todosLosProductos.isEmpty()) result.messageRes else null
                    )
                }
            }
        }
    }

    private fun filtrar(categoria: String): List<Product> =
        if (categoria == CATEGORIA_TODO) todosLosProductos
        else todosLosProductos.filter { it.category == categoria }
}

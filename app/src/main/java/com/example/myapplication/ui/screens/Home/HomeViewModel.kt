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
        getProductos()
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

    fun getProductos() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

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
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    private fun filtrar(categoria: String): List<Product> =
        if (categoria == CATEGORIA_TODO) todosLosProductos
        else todosLosProductos.filter { it.category == categoria }
}

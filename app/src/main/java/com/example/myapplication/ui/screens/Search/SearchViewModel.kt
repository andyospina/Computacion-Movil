package com.example.myapplication.ui.screens.Search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState = _uiState.asStateFlow()

    // El backend no tiene búsqueda: se descargan los productos y se filtra aquí.
    private var catalogo: List<Product> = emptyList()

    init {
        getProductos()
    }

    fun getProductos() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            when (val result = productRepository.getProducts()) {
                is Result.Success -> {
                    catalogo = result.data
                    _uiState.update {
                        it.copy(resultados = buscar(it.query), isLoading = false, errorMessageRes = null)
                    }
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    fun updateQuery(value: String) {
        _uiState.update { it.copy(query = value, resultados = buscar(value)) }
    }

    private fun buscar(query: String): List<Product> =
        if (query.isBlank()) emptyList()
        else catalogo.filter { it.name.normalizar().contains(query.normalizar()) }

    private fun String.normalizar(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
}

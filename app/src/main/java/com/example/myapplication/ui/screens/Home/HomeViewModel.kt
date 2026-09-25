package com.example.myapplication.ui.screens.Home

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
        getProductos(_uiState.value.categoriaSeleccionada)
        getProfileImage()
    }

    fun updateCategoria(categoria: String) {
        getProductos(categoria)
    }

    fun getProfileImage() {
        _uiState.update { it.copy(profileImageUrl = authRepository.currentUser?.photoUrl?.toString()) }
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

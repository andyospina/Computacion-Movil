package com.example.myapplication.ui.screens.NewReview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewReviewViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NewReviewState())
    val uiState = _uiState.asStateFlow()

    fun updatePuntuacion(value: Int) {
        _uiState.update { it.copy(puntuacion = value) }
    }

    fun updateTitulo(value: String) {
        _uiState.update { it.copy(titulo = value) }
    }

    fun updateResena(value: String) {
        _uiState.update { it.copy(resena = value) }
    }

    fun updateUsuario(value: String) {
        _uiState.update { it.copy(usuario = value) }
    }

    fun updateCorreo(value: String) {
        _uiState.update { it.copy(correo = value) }
    }

    fun publicarButtonPress() {
        _uiState.update { it.copy(publicado = true) }
    }

    fun onPublicado() {
        _uiState.update { it.copy(publicado = false) }
    }
}

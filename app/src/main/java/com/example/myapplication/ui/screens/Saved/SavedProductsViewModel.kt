package com.example.myapplication.ui.screens.Saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Productos guardados del usuario; la lista cambia en vivo al guardar o quitar desde cualquier pantalla. */
@HiltViewModel
class SavedProductsViewModel @Inject constructor(
    private val savedRepository: SavedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedProductsState())
    val uiState = _uiState.asStateFlow()

    private var observacionJob: Job? = null

    init {
        observar()
    }

    fun reintentar() {
        observacionJob?.cancel()
        observar()
    }

    fun quitar(productId: String) {
        viewModelScope.launch {
            val result = savedRepository.setSaved(productId, saved = false)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun observar() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        observacionJob = viewModelScope.launch {
            savedRepository.observeSavedProducts().collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(productos = result.data, isLoading = false, errorMessageRes = null)
                    }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = result.messageRes)
                    }
                }
            }
        }
    }
}

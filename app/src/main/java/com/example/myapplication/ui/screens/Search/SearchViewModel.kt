package com.example.myapplication.ui.screens.Search

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalProductProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState = _uiState.asStateFlow()

    fun updateQuery(value: String) {
        _uiState.update {
            it.copy(query = value, resultados = LocalProductProvider.search(value))
        }
    }
}

package com.example.myapplication.ui.screens.Profile

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.LocalUserProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        getPerfil()
    }

    private fun getPerfil() {
        _uiState.update {
            it.copy(
                user = LocalUserProvider.currentUser,
                misResenas = LocalReviewProvider.byUser("Emily")
            )
        }
    }
}

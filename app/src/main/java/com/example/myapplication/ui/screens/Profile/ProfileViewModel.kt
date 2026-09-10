package com.example.myapplication.ui.screens.Profile

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        getPerfil()
    }

    private fun getPerfil() {
        _uiState.update {
            it.copy(
                user = LocalUserProvider.currentUser,
                misResenas = LocalReviewProvider.byUser("Emily"),
                email = authRepository.currentUser?.email.orEmpty()
            )
        }
    }

    fun logoutButtonPress() {
        authRepository.signOut()
        _uiState.update { it.copy(loggedOut = true) }
    }

    fun onLoggedOut() {
        _uiState.update { it.copy(loggedOut = false) }
    }
}

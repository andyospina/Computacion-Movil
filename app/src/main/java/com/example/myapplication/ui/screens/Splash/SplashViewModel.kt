package com.example.myapplication.ui.screens.Splash

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigateHome = MutableStateFlow<Boolean?>(null)
    val navigateHome = _navigateHome.asStateFlow()

    init {
        checkUser()
    }

    private fun checkUser() {
        _navigateHome.value = authRepository.currentUser != null
    }
}

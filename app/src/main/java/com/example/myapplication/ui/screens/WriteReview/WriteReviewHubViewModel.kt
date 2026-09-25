package com.example.myapplication.ui.screens.WriteReview

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WriteReviewHubViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewHubState())
    val uiState = _uiState.asStateFlow()

    init {
        getProfileImage()
    }

    fun getProfileImage() {
        _uiState.update { it.copy(profileImageUrl = authRepository.currentUser?.photoUrl?.toString()) }
    }
}

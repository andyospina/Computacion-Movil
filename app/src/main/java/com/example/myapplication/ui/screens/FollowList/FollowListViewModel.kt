package com.example.myapplication.ui.screens.FollowList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FollowStats
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Seguidores y seguidos de un usuario. Las listas se actualizan cuando alguien sigue o deja de seguir. */
@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowListState())
    val uiState = _uiState.asStateFlow()

    private var observacionJob: Job? = null

    fun getListas(userId: String, tab: FollowTab) {
        if (_uiState.value.userId == userId && observacionJob?.isActive == true) return
        _uiState.update { it.copy(userId = userId, tab = tab) }
        observar(userId)
    }

    fun reintentar() {
        observacionJob?.cancel()
        observar(_uiState.value.userId)
    }

    fun updateTab(tab: FollowTab) {
        _uiState.update { it.copy(tab = tab) }
    }

    private fun observar(userId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        observacionJob = viewModelScope.launch {
            // Solo se piden los perfiles cuando cambian los ids, no en cada emisión del listener
            followRepository.observeFollowStats(userId).distinctUntilChanged().collect { result ->
                when (result) {
                    is Result.Success -> cargarUsuarios(result.data)
                    is Result.Failure -> falla(result.messageRes)
                }
            }
        }
    }

    private suspend fun cargarUsuarios(stats: FollowStats) {
        val seguidores = when (val result = userRepository.getUsersByIds(stats.followerIds)) {
            is Result.Success -> result.data
            is Result.Failure -> return falla(result.messageRes)
        }
        val seguidos = when (val result = userRepository.getUsersByIds(stats.followingIds)) {
            is Result.Success -> result.data
            is Result.Failure -> return falla(result.messageRes)
        }
        _uiState.update {
            it.copy(seguidores = seguidores, seguidos = seguidos, isLoading = false, errorMessageRes = null)
        }
    }

    private fun falla(messageRes: Int) {
        _uiState.update { it.copy(isLoading = false, errorMessageRes = messageRes) }
    }
}

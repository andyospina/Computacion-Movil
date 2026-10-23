package com.example.myapplication.ui.screens.Feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Feed «Siguiendo»: solo las reseñas de los usuarios que sigue el usuario actual, en tiempo real. */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedState())
    val uiState = _uiState.asStateFlow()

    private var feedJob: Job? = null

    init {
        observarFeed()
    }

    fun reintentar() {
        feedJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        observarFeed()
    }

    fun toggleLike(resena: Review) {
        viewModelScope.launch {
            val result = reviewRepository.toggleLike(resena)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    // Al seguir o dejar de seguir a alguien cambia la lista de ids y flatMapLatest reemplaza la
    // consulta de reseñas por una nueva con los ids actualizados.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observarFeed() {
        feedJob = viewModelScope.launch {
            followRepository.observeMyFollowingIds()
                .flatMapLatest { seguidos ->
                    when (seguidos) {
                        is Result.Success -> reviewRepository.observeReviewsByUsers(seguidos.data)
                            .map { resenas -> seguidos.data.size to resenas }
                        is Result.Failure -> flowOf(0 to seguidos)
                    }
                }
                .collect { (cantidadSeguidos, result) ->
                    when (result) {
                        is Result.Success -> _uiState.update {
                            it.copy(
                                resenas = result.data,
                                cantidadSeguidos = cantidadSeguidos,
                                isLoading = false,
                                errorMessageRes = null
                            )
                        }
                        is Result.Failure -> _uiState.update {
                            it.copy(isLoading = false, errorMessageRes = result.messageRes)
                        }
                    }
                }
        }
    }
}

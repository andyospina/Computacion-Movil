package com.example.myapplication.ui.screens.Notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Bandeja de notificaciones (likes y seguidores nuevos) que escriben las Cloud Functions. */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState = _uiState.asStateFlow()

    private var notificacionesJob: Job? = null

    init {
        getNotificaciones()
    }

    fun reintentar() {
        notificacionesJob?.cancel()
        getNotificaciones()
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            // El listener refleja el cambio de `read`
            val result = notificationRepository.markAllAsRead(_uiState.value.notificaciones)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }

    private fun getNotificaciones() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        notificacionesJob = viewModelScope.launch {
            notificationRepository.observeNotifications().collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(notificaciones = result.data, isLoading = false, errorMessageRes = null)
                    }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = result.messageRes)
                    }
                }
            }
        }
    }
}

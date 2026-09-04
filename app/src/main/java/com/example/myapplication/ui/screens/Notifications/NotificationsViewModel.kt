package com.example.myapplication.ui.screens.Notifications

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LocalNotificationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState = _uiState.asStateFlow()

    fun marcarTodasLeidas() {
        _uiState.update {
            it.copy(
                leidas = true,
                notificaciones = LocalNotificationProvider.notifications.map { item ->
                    item.copy(highlighted = false)
                }
            )
        }
    }
}

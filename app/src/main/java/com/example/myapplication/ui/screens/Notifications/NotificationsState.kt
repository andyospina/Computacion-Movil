package com.example.myapplication.ui.screens.Notifications

import androidx.annotation.StringRes
import com.example.myapplication.data.AppNotification

data class NotificationsState(
    val notificaciones: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

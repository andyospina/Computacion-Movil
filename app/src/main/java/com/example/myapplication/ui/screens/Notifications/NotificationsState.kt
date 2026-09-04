package com.example.myapplication.ui.screens.Notifications

import com.example.myapplication.data.LocalNotificationProvider
import com.example.myapplication.data.NotificationItem

data class NotificationsState(
    val leidas: Boolean = false,
    val notificaciones: List<NotificationItem> = LocalNotificationProvider.notifications
)

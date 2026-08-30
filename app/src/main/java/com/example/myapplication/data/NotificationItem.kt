package com.example.myapplication.data

import androidx.compose.ui.graphics.vector.ImageVector

data class NotificationItem(
    val id: String,
    val icon: ImageVector,
    val highlighted: Boolean,
    val message: String,
    val time: String
)

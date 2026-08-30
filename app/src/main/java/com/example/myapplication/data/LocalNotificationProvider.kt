package com.example.myapplication.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Star

object LocalNotificationProvider {
    val notifications = listOf(
        NotificationItem(
            id = "n1",
            icon = Icons.Filled.Star,
            highlighted = true,
            message = "14 personas marcaron tu reseña de Grabadora de voz AI como útil",
            time = "Hace 2 horas"
        ),
        NotificationItem(
            id = "n2",
            icon = Icons.Filled.CheckCircle,
            highlighted = true,
            message = "Tu reseña de Sérum facial vitamina C fue verificada y publicada",
            time = "Ayer"
        ),
        NotificationItem(
            id = "n3",
            icon = Icons.Filled.ChatBubble,
            highlighted = false,
            message = "La marca respondió a tu reseña: \"Gracias por tu comentario, Emily...\"",
            time = "Hace 3 días"
        ),
        NotificationItem(
            id = "n4",
            icon = Icons.Filled.EditNote,
            highlighted = false,
            message = "Compraste Cafetera espresso mini hace 2 semanas · ¿cómo te fue?",
            time = "Hace 5 días"
        )
    )
}

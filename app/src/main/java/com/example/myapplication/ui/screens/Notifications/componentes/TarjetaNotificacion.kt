package com.example.myapplication.ui.screens.Notifications.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.AppNotification
import com.example.myapplication.data.NotificationType
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.GraySecondary

/** Las no leídas se resaltan. El texto se arma aquí con el tipo y quién realizó la acción. */
@Composable
fun TarjetaNotificacion(
    notificacion: AppNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noLeida = !notificacion.read

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (noLeida) BorderColor.copy(alpha = 0.25f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(width = 1.dp, color = BorderColor.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color = MaterialTheme.colorScheme.onBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (notificacion.type) {
                    NotificationType.LIKE -> Icons.Filled.Favorite
                    NotificationType.FOLLOW -> Icons.Filled.PersonAdd
                    NotificationType.UNKNOWN -> Icons.Filled.Notifications
                },
                contentDescription = null,
                tint = ElectricLime,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = when (notificacion.type) {
                    NotificationType.LIKE -> stringResource(
                        R.string.notifications_like_message, notificacion.actorName, notificacion.reviewTitle
                    )
                    NotificationType.FOLLOW -> stringResource(
                        R.string.notifications_follow_message, notificacion.actorName
                    )
                    NotificationType.UNKNOWN -> stringResource(R.string.notifications_unknown_message)
                },
                fontSize = 14.sp,
                fontWeight = if (noLeida) FontWeight.Bold else FontWeight.Normal
            )
            Text(text = notificacion.date, fontSize = 12.sp, color = GraySecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaNotificacionPreview() {
    TarjetaNotificacion(
        notificacion = AppNotification(
            id = "n1",
            type = NotificationType.LIKE,
            actorId = "u1",
            actorName = "Ada Lovelace",
            reviewTitle = "Sonido excelente",
            productId = "p1",
            date = "Oct 21, 2026",
            read = false
        ),
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

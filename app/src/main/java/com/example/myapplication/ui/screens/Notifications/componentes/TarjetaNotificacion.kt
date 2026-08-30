package com.example.myapplication.ui.screens.Notifications.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.LocalNotificationProvider
import com.example.myapplication.data.NotificationItem
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun TarjetaNotificacion(
    notificacion: NotificationItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (notificacion.highlighted) BorderColor.copy(alpha = 0.25f) else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(width = 1.dp, color = BorderColor.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
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
                imageVector = notificacion.icon,
                contentDescription = null,
                tint = ElectricLime,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = notificacion.message, fontSize = 14.sp)
            Text(text = notificacion.time, fontSize = 12.sp, color = GraySecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaNotificacionPreview() {
    TarjetaNotificacion(
        notificacion = LocalNotificationProvider.notifications.first(),
        modifier = Modifier.padding(16.dp)
    )
}

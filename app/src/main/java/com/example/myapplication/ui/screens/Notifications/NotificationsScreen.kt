package com.example.myapplication.ui.screens.Notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.LocalNotificationProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Notifications.componentes.TarjetaNotificacion
import com.example.myapplication.ui.theme.Ink

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    var leidas by remember { mutableStateOf(false) }
    val notificaciones = remember(leidas) {
        if (leidas) {
            LocalNotificationProvider.notifications.map { it.copy(highlighted = false) }
        } else {
            LocalNotificationProvider.notifications
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                navigation = TopBarNavigation.BACK,
                onNavigationClick = onBackClick,
                trailingContent = {
                    Text(
                        text = "Marcar leídas",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { leidas = true }
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            Text(text = "Notificaciones", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(notificaciones, key = { it.id }) { notificacion ->
                    TarjetaNotificacion(notificacion = notificacion)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "NotificationsScreen - Preview")
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(onBackClick = {})
}

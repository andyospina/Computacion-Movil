package com.example.myapplication.ui.screens.Notificaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Notificacion
import com.example.myapplication.ui.screens.Notificaciones.componentes.TarjetaNotificacion
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun NotificacionesScreen(
    modifier: Modifier = Modifier,
    notificaciones: List<Notificacion>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Notificaciones", fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }

        items(items = notificaciones, key = { it.id }) { notificacion ->
            TarjetaNotificacion(notificacion = notificacion)
        }
    }
}

@Preview(showBackground = true, name = "NotificacionesScreen - Preview")
@Composable
fun NotificacionesScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        NotificacionesScreen(notificaciones = DatosLocales.notificaciones)
    }
}

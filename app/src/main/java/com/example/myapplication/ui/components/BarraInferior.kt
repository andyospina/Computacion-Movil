package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.MyApplicationTheme

data class ItemBarraInferior(val ruta: String, val etiqueta: String, val icono: String)

/**
 * Barra inferior de 4 pestañas (Inicio / Buscar / Reseñar / Perfil). En el
 * diseño los ítems inactivos se atenúan con opacidad, no con un color
 * distinto ni un indicador — por eso se implementa a medida en vez de usar
 * NavigationBar de Material 3.
 */
@Composable
fun BarraInferior(
    items: List<ItemBarraInferior>,
    rutaActual: String?,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = BorderLight)
            .padding(vertical = 10.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { item ->
            val activo = item.ruta == rutaActual

            Column(
                modifier = Modifier
                    .clickable { onItemClick(item.ruta) }
                    .alpha(if (activo) 1f else 0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = item.icono, fontSize = 16.sp)
                Text(
                    text = item.etiqueta,
                    fontSize = 10.sp,
                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarraInferiorPreview() {
    MyApplicationTheme(darkTheme = false) {
        BarraInferior(
            items = listOf(
                ItemBarraInferior("home", "Inicio", "🏠"),
                ItemBarraInferior("buscar", "Buscar", "🔍"),
                ItemBarraInferior("resenar", "Reseñar", "✍️"),
                ItemBarraInferior("perfil", "Perfil", "👤")
            ),
            rutaActual = "home",
            onItemClick = {}
        )
    }
}

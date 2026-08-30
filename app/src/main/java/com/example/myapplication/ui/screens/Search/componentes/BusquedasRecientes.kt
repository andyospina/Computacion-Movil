package com.example.myapplication.ui.screens.Search.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BorderColor

val busquedasRecientes = listOf("grabadora de voz", "sérum vitamina c", "cafetera")

@Composable
fun BusquedasRecientes(
    modifier: Modifier = Modifier,
    onBusquedaClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "Búsquedas recientes", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            busquedasRecientes.forEach { busqueda ->
                Text(
                    text = busqueda,
                    modifier = Modifier
                        .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(20.dp))
                        .clickable { onBusquedaClick(busqueda) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusquedasRecientesPreview() {
    BusquedasRecientes(
        modifier = Modifier.padding(16.dp),
        onBusquedaClick = {}
    )
}

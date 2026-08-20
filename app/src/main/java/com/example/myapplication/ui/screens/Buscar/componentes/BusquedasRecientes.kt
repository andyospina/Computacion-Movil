package com.example.myapplication.ui.screens.Buscar.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun BusquedasRecientes(
    busquedas: List<String>,
    onBusquedaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "Búsquedas recientes", fontSize = 13.sp, fontWeight = FontWeight.Bold)

        FlowRow(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val shape = RoundedCornerShape(999.dp)
            busquedas.forEach { busqueda ->
                Text(
                    text = busqueda,
                    modifier = Modifier
                        .clip(shape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                        .clickable { onBusquedaClick(busqueda) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusquedasRecientesPreview() {
    MyApplicationTheme(darkTheme = false) {
        BusquedasRecientes(busquedas = DatosLocales.busquedasRecientes, onBusquedaClick = {})
    }
}

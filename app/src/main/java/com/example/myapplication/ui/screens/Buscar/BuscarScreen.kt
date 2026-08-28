package com.example.myapplication.ui.screens.Buscar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.ui.components.TarjetaProducto
import com.example.myapplication.ui.screens.Buscar.componentes.BusquedasRecientes
import com.example.myapplication.ui.screens.Buscar.componentes.CampoBusquedaProducto
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Búsqueda de productos ("11 Busqueda"). El texto de búsqueda es estado
 * propio de esta pantalla (hoisted aquí, puramente de UI).
 */
@Composable
fun BuscarScreen(
    modifier: Modifier = Modifier,
    productos: List<Producto>,
    busquedasRecientes: List<String>,
    onProductoClick: (Producto) -> Unit
) {
    var texto by remember { mutableStateOf("") }

    val resultados = if (texto.isBlank()) {
        emptyList()
    } else {
        productos.filter { it.nombre.contains(texto, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        CampoBusquedaProducto(texto = texto, onTextoChange = { texto = it })

        if (texto.isNotBlank()) {
            Text(
                text = "${resultados.size} resultados para \"$texto\"",
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = resultados, key = { it.id }) { producto ->
                TarjetaProducto(producto = producto, onClick = { onProductoClick(producto) })
            }
        }

        BusquedasRecientes(
            busquedas = busquedasRecientes,
            onBusquedaClick = { texto = it }
        )
    }
}

@Preview(showBackground = true, name = "BuscarScreen - Preview")
@Composable
fun BuscarScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        BuscarScreen(
            productos = DatosLocales.productos,
            busquedasRecientes = DatosLocales.busquedasRecientes,
            onProductoClick = {}
        )
    }
}

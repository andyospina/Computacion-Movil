package com.example.myapplication.ui.screens.HomeFeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.ui.components.ChipFiltro
import com.example.myapplication.ui.components.TarjetaProducto
import com.example.myapplication.ui.screens.HomeFeed.componentes.CampoBuscarProducto
import com.example.myapplication.ui.theme.MyApplicationTheme

private val CATEGORIAS = listOf("Todo", "Tecnología", "Belleza", "Hogar")

/**
 * Home del usuario autenticado ("06 Home feed"): catálogo de productos con
 * categorías y tendencias. El filtro de categoría es estado propio de esta
 * pantalla (hoisted aquí, ya que es puramente de UI).
 */
@Composable
fun HomeFeedScreen(
    modifier: Modifier = Modifier,
    productos: List<Producto>,
    onBuscarClick: () -> Unit,
    onProductoClick: (Producto) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todo") }

    val productosFiltrados = if (categoriaSeleccionada == "Todo") {
        productos
    } else {
        productos.filter { it.categoria == categoriaSeleccionada }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CampoBuscarProducto(onClick = onBuscarClick)
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CATEGORIAS) { categoria ->
                    ChipFiltro(
                        texto = categoria,
                        seleccionado = categoria == categoriaSeleccionada,
                        onClick = { categoriaSeleccionada = categoria }
                    )
                }
            }
        }

        item {
            Text(
                text = "Tendencias esta semana",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(items = productosFiltrados, key = { it.id }) { producto ->
            TarjetaProducto(
                producto = producto,
                onClick = { onProductoClick(producto) }
            )
        }
    }
}

@Preview(showBackground = true, name = "HomeFeedScreen - Preview")
@Composable
fun HomeFeedScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        HomeFeedScreen(
            productos = DatosLocales.productos,
            onBuscarClick = {},
            onProductoClick = {}
        )
    }
}

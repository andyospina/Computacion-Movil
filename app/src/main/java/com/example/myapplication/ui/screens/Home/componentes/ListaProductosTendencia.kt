package com.example.myapplication.ui.screens.Home.componentes

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.Product
import com.example.myapplication.ui.components.ProductListItem

@Composable
fun ListaProductosTendencia(
    productos: List<Product>,
    onProductoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        items(productos, key = { it.id }) { producto ->
            ProductListItem(
                product = producto,
                onClick = { onProductoClick(producto.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaProductosTendenciaPreview() {
    ListaProductosTendencia(
        productos = LocalProductProvider.trending(),
        onProductoClick = {}
    )
}

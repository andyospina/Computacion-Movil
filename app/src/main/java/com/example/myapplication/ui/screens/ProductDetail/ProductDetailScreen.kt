package com.example.myapplication.ui.screens.ProductDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.ProductDetail.componentes.BotonesAccionProducto
import com.example.myapplication.ui.screens.ProductDetail.componentes.FotoProducto
import com.example.myapplication.ui.screens.ProductDetail.componentes.InfoProducto
import com.example.myapplication.ui.screens.ProductDetail.componentes.ResenasDestacadas
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun ProductDetailScreen(
    productId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onEscribirResenaClick: (String) -> Unit,
    onVerTodasResenasClick: (String) -> Unit
) {
    val producto = remember(productId) { LocalProductProvider.findById(productId) }
    val resenasDestacadas = remember(productId) { LocalReviewProvider.featuredForProduct(productId) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                title = "Producto",
                navigation = TopBarNavigation.BACK,
                onNavigationClick = onBackClick,
                trailingContent = {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = GraySecondary)
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            FotoProducto()

            Column(modifier = Modifier.padding(20.dp)) {

                InfoProducto(producto = producto)

                Spacer(modifier = Modifier.height(16.dp))

                BotonesAccionProducto(
                    modifier = Modifier.fillMaxWidth(),
                    onEscribirResenaClick = { onEscribirResenaClick(productId) },
                    onGuardarClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                ResenasDestacadas(
                    resenas = resenasDestacadas,
                    onVerTodasClick = { onVerTodasResenasClick(productId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "ProductDetailScreen - Preview")
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailScreen(
        productId = "grabadora-voz-ai",
        onBackClick = {},
        onEscribirResenaClick = {},
        onVerTodasResenasClick = {}
    )
}

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: ProductDetailViewModel = viewModel(),
    onBackClick: () -> Unit,
    onEscribirResenaClick: (String) -> Unit,
    onVerTodasResenasClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.getProducto(productId)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            title = "Producto",
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick,
            trailingContent = {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = GraySecondary)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            FotoProducto()

            Column(modifier = Modifier.padding(20.dp)) {

                uiState.producto?.let { producto ->
                    InfoProducto(producto = producto)

                    Spacer(modifier = Modifier.height(16.dp))

                    BotonesAccionProducto(
                        modifier = Modifier.fillMaxWidth(),
                        onEscribirResenaClick = { onEscribirResenaClick(productId) },
                        onGuardarClick = {}
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ResenasDestacadas(
                        resenas = uiState.resenasDestacadas,
                        onVerTodasClick = { onVerTodasResenasClick(productId) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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

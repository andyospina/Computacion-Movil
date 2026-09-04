package com.example.myapplication.ui.screens.Reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.myapplication.ui.screens.Reviews.componentes.EncabezadoResenas
import com.example.myapplication.ui.screens.Reviews.componentes.FiltrosResenas
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun ReviewsListScreen(
    productId: String,
    modifier: Modifier = Modifier,
    viewModel: ReviewsListViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.init(productId)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick,
            trailingContent = {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = GraySecondary)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            uiState.producto?.let { producto ->
                EncabezadoResenas(producto = producto, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }

            FiltrosResenas(
                orden = uiState.orden,
                conFotos = uiState.conFotos,
                onOrdenChange = viewModel::updateOrden,
                onConFotosChange = viewModel::updateConFotos
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.resenasFiltradas.take(uiState.cantidadVisible), key = { it.id }) { resena ->
                    TarjetaResena(resena = resena)
                }

                if (uiState.cantidadVisible < uiState.resenasFiltradas.size) {
                    item {
                        OutlinedButton(
                            onClick = viewModel::cargarMas,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Cargar más reseñas")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ReviewsListScreen - Preview")
@Composable
fun ReviewsListScreenPreview() {
    ReviewsListScreen(
        productId = "grabadora-voz-ai",
        onBackClick = {}
    )
}

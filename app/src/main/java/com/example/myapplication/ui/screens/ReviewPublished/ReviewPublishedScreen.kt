package com.example.myapplication.ui.screens.ReviewPublished

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.ReviewPublished.componentes.BotonesResumenPublicado
import com.example.myapplication.ui.screens.ReviewPublished.componentes.IconoPublicado
import com.example.myapplication.ui.screens.ReviewPublished.componentes.MensajePublicado
import com.example.myapplication.ui.screens.ReviewPublished.componentes.TarjetaResumenPublicado
import com.example.myapplication.ui.theme.Ink

@Composable
fun ReviewPublishedScreen(
    productId: String,
    rating: Int,
    modifier: Modifier = Modifier,
    viewModel: ReviewPublishedViewModel = viewModel(),
    onCloseClick: () -> Unit,
    onVerMiResenaClick: () -> Unit,
    onVolverInicioClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.getProducto(productId)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.NONE,
            trailingContent = {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = Ink,
                    modifier = Modifier
                        .clickable { onCloseClick() }
                        .padding(4.dp)
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            IconoPublicado()

            Spacer(modifier = Modifier.height(20.dp))

            MensajePublicado()

            Spacer(modifier = Modifier.height(28.dp))

            uiState.producto?.let { producto ->
                TarjetaResumenPublicado(
                    producto = producto,
                    calificacion = rating,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonesResumenPublicado(
                modifier = Modifier.fillMaxWidth(),
                onVerMiResenaClick = onVerMiResenaClick,
                onVolverInicioClick = onVolverInicioClick
            )
        }
    }
}

@Preview(showBackground = true, name = "ReviewPublishedScreen - Preview")
@Composable
fun ReviewPublishedScreenPreview() {
    ReviewPublishedScreen(
        productId = "grabadora-voz-ai",
        rating = 4,
        onCloseClick = {},
        onVerMiResenaClick = {},
        onVolverInicioClick = {}
    )
}

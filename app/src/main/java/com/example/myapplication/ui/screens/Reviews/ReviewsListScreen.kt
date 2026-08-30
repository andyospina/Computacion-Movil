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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Reviews.componentes.EncabezadoResenas
import com.example.myapplication.ui.screens.Reviews.componentes.FiltrosResenas
import com.example.myapplication.ui.screens.Reviews.componentes.OrdenResenas
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary

private const val PAGE_SIZE = 4

@Composable
fun ReviewsListScreen(
    productId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val producto = remember(productId) { LocalProductProvider.findById(productId) }
    val todasLasResenas = remember(productId) { LocalReviewProvider.forProduct(productId) }

    var orden by remember { mutableStateOf(OrdenResenas.RECIENTES) }
    var conFotos by remember { mutableStateOf(false) }
    var cantidadVisible by remember { mutableIntStateOf(PAGE_SIZE) }

    val resenasFiltradas = remember(orden, conFotos, todasLasResenas) {
        todasLasResenas
            .let { lista -> if (conFotos) lista.filter { it.hasPhoto } else lista }
            .let { lista ->
                if (orden == OrdenResenas.MEJOR_VALORADAS) lista.sortedByDescending { it.rating }
                else lista
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            EncabezadoResenas(producto = producto, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            FiltrosResenas(
                orden = orden,
                conFotos = conFotos,
                onOrdenChange = { orden = it },
                onConFotosChange = { conFotos = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resenasFiltradas.take(cantidadVisible), key = { it.id }) { resena ->
                    TarjetaResena(resena = resena)
                }

                if (cantidadVisible < resenasFiltradas.size) {
                    item {
                        OutlinedButton(
                            onClick = { cantidadVisible += PAGE_SIZE },
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

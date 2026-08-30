package com.example.myapplication.ui.screens.Search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.ProductListItem
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Search.componentes.BusquedasRecientes
import com.example.myapplication.ui.screens.Search.componentes.CampoBusqueda
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onProductoClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val resultados = remember(query) { LocalProductProvider.search(query) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                navigation = TopBarNavigation.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            CampoBusqueda(
                modifier = Modifier.fillMaxWidth(),
                query = query,
                onQueryChange = { query = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (query.isBlank()) {
                BusquedasRecientes(
                    onBusquedaClick = { query = it }
                )
            } else {
                Text(
                    text = "${resultados.size} resultados para \"$query\"",
                    color = GraySecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    items(resultados, key = { it.id }) { producto ->
                        ProductListItem(
                            product = producto,
                            onClick = { onProductoClick(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SearchScreen - Preview")
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        onBackClick = {},
        onProductoClick = {}
    )
}

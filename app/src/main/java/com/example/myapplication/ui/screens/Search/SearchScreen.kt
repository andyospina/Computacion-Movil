package com.example.myapplication.ui.screens.Search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.ProductListItem
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Search.componentes.BusquedasRecientes
import com.example.myapplication.ui.screens.Search.componentes.CampoBusqueda
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(),
    onBackClick: () -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            CampoBusqueda(
                modifier = Modifier.fillMaxWidth(),
                query = uiState.query,
                onQueryChange = viewModel::updateQuery
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.query.isBlank()) {
                BusquedasRecientes(
                    onBusquedaClick = viewModel::updateQuery
                )
            } else {
                Text(
                    text = "${uiState.resultados.size} resultados para \"${uiState.query}\"",
                    color = GraySecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.resultados, key = { it.id }) { producto ->
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

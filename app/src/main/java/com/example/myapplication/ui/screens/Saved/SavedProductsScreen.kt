package com.example.myapplication.ui.screens.Saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.ProductListItem
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun SavedProductsScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedProductsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(navigation = TopBarNavigation.BACK, onNavigationClick = onBackClick)

        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text(text = stringResource(R.string.saved_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            CargaOError(
                isLoading = uiState.isLoading,
                errorMessageRes = uiState.errorMessageRes,
                onRetry = viewModel::reintentar
            ) {
                if (uiState.productos.isEmpty()) {
                    Text(text = stringResource(R.string.saved_empty), color = GraySecondary)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.productos, key = { it.id }) { producto ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProductListItem(
                                    product = producto,
                                    onClick = { onProductoClick(producto.id) },
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { viewModel.quitar(producto.id) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                                ) {
                                    Text(stringResource(R.string.saved_remove))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

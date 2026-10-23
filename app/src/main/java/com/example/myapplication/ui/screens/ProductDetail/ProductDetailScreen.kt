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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
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
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEscribirResenaClick: (String) -> Unit,
    onVerTodasResenasClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.getProducto(productId)
    }

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    ProductDetailContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = viewModel::reintentar,
        onBackClick = onBackClick,
        onEscribirResenaClick = { onEscribirResenaClick(productId) },
        onVerTodasResenasClick = { onVerTodasResenasClick(productId) },
        onUserClick = onUserClick,
        onLikeClick = viewModel::toggleLike
    )
}

@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    uiState: ProductDetailState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    onEscribirResenaClick: () -> Unit,
    onVerTodasResenasClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onLikeClick: (Review) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            title = stringResource(R.string.product_detail_title),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick,
            trailingContent = {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.content_description_more_options),
                    tint = GraySecondary
                )
            }
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = onRetry
        ) {
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
                            onEscribirResenaClick = onEscribirResenaClick,
                            onGuardarClick = {}
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ResenasDestacadas(
                            resenas = uiState.resenasDestacadas,
                            onVerTodasClick = onVerTodasResenasClick,
                            onAuthorClick = onUserClick,
                            onLikeClick = onLikeClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ProductDetailScreen - Preview")
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailContent(
        uiState = ProductDetailState(),
        onRetry = {},
        onBackClick = {},
        onEscribirResenaClick = {},
        onVerTodasResenasClick = {},
        onUserClick = {},
        onLikeClick = {}
    )
}

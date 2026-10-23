package com.example.myapplication.ui.screens.Feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    FeedContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = viewModel::reintentar,
        onUserClick = onUserClick,
        onProductoClick = onProductoClick,
        onLikeClick = viewModel::toggleLike
    )
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    uiState: FeedState,
    onRetry: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit,
    onLikeClick: (Review) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {

        BarraSuperior(
            title = stringResource(R.string.feed_title),
            navigation = TopBarNavigation.NONE
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = onRetry
        ) {
            if (uiState.resenas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(
                            if (uiState.cantidadSeguidos == 0) R.string.feed_empty_no_following
                            else R.string.feed_empty_no_reviews
                        ),
                        color = GraySecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.resenas, key = { it.id }) { resena ->
                        TarjetaResena(
                            resena = resena,
                            onAuthorClick = { onUserClick(resena.userId) },
                            onLikeClick = { onLikeClick(resena) },
                            onProductClick = { onProductoClick(resena.productId) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "FeedScreen - Preview")
@Composable
fun FeedScreenPreview() {
    FeedContent(
        uiState = FeedState(isLoading = false),
        onRetry = {},
        onUserClick = {},
        onProductoClick = {},
        onLikeClick = {}
    )
}

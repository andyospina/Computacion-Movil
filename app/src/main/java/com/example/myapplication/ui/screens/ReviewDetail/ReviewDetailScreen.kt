package com.example.myapplication.ui.screens.ReviewDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena

@Composable
fun ReviewDetailScreen(
    reviewId: String,
    modifier: Modifier = Modifier,
    viewModel: ReviewDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reviewId) { viewModel.cargar(reviewId) }

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(
            title = stringResource(R.string.review_detail_title),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = viewModel::reintentar
        ) {
            uiState.review?.let { review ->
                TarjetaResena(
                    resena = review,
                    modifier = Modifier.padding(16.dp),
                    onAuthorClick = { onUserClick(review.userId) },
                    onLikeClick = viewModel::toggleLike,
                    onProductClick = { onProductoClick(review.productId) }
                )
            }
        }
    }
}

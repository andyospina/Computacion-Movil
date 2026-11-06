package com.example.myapplication.ui.screens.LikedReviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun LikedReviewsScreen(
    modifier: Modifier = Modifier,
    viewModel: LikedReviewsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    Column(modifier = modifier.fillMaxSize().testTag("likedReviewsScreen")) {
        BarraSuperior(navigation = TopBarNavigation.BACK, onNavigationClick = onBackClick)

        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text(text = stringResource(R.string.liked_reviews_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            CargaOError(
                isLoading = uiState.isLoading,
                errorMessageRes = uiState.errorMessageRes,
                onRetry = viewModel::reintentar
            ) {
                if (uiState.resenas.isEmpty()) {
                    Text(
                        text = stringResource(R.string.liked_reviews_empty),
                        color = GraySecondary,
                        modifier = Modifier.testTag("likedReviewsEmpty")
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.resenas, key = { it.id }) { resena ->
                            TarjetaResena(
                                resena = resena,
                                onAuthorClick = { onUserClick(resena.userId) },
                                onLikeClick = { viewModel.toggleLike(resena) },
                                onProductClick = { onProductoClick(resena.productId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

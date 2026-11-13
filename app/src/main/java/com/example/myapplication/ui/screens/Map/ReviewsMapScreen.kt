package com.example.myapplication.ui.screens.Map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Map.componentes.OsmMapView
import com.example.myapplication.ui.screens.Map.componentes.TarjetaResenaMapa

@Composable
fun ReviewsMapScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewsMapViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(
            title = stringResource(R.string.map_title),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = viewModel::reintentar
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                OsmMapView(
                    reviews = uiState.reviews,
                    selectedReviewId = uiState.selectedReviewId,
                    onMarkerClick = viewModel::onMarcadorClick,
                    onMapClick = viewModel::onSeleccionCerrada,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(12.dp)
                ) {
                    Text(
                        text = if (uiState.reviews.isEmpty()) {
                            stringResource(R.string.map_empty)
                        } else {
                            pluralStringResource(
                                R.plurals.map_reviews_count, uiState.reviews.size, uiState.reviews.size
                            )
                        },
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                uiState.selected?.let { review ->
                    TarjetaResenaMapa(
                        review = review,
                        onProductClick = { onProductoClick(review.productId) },
                        onUserClick = { onUserClick(review.userId) },
                        onClose = viewModel::onSeleccionCerrada,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
                    )
                }
            }
        }
    }
}

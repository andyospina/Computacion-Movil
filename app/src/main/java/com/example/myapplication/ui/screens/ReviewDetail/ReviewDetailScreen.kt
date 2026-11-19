package com.example.myapplication.ui.screens.ReviewDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.ReviewDetail.componentes.DialogoReportarResena
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.DeepLime

@Composable
fun ReviewDetailScreen(
    reviewId: String,
    modifier: Modifier = Modifier,
    viewModel: ReviewDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit,
    onComentariosClick: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reviewId) { viewModel.cargar(reviewId) }

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)
    MensajeErrorAccion(messageRes = uiState.infoMessageRes, onShown = viewModel::onInfoMessageShown)

    if (uiState.showReportDialog) {
        DialogoReportarResena(
            isSending = uiState.isReporting,
            onEnviar = viewModel::enviarReporte,
            onCancelar = viewModel::onReportDismissed
        )
    }

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
                // Con scroll: en pantallas pequeñas o con letra grande la tarjeta y el reporte no caben
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    TarjetaResena(
                        resena = review,
                        modifier = Modifier.padding(16.dp),
                        onAuthorClick = { onUserClick(review.userId) },
                        onLikeClick = viewModel::toggleLike,
                        onProductClick = { onProductoClick(review.productId) },
                        onCommentsClick = onComentariosClick?.let { { it(review.id) } }
                    )

                    if (uiState.canReport) {
                        TextButton(
                            onClick = viewModel::onReportClick,
                            enabled = !uiState.alreadyReported,
                            modifier = Modifier.padding(horizontal = 8.dp).testTag("btnReport")
                        ) {
                            Text(
                                text = stringResource(
                                    if (uiState.alreadyReported) R.string.report_already else R.string.report_action
                                ),
                                color = if (uiState.alreadyReported) GraySecondary else DeepLime
                            )
                        }
                    }
                }
            }
        }
    }
}

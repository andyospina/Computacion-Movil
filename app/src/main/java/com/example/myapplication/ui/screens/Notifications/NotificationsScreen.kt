package com.example.myapplication.ui.screens.Notifications

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.data.AppNotification
import com.example.myapplication.data.NotificationType
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Notifications.componentes.TarjetaNotificacion
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    NotificationsContent(
        modifier = modifier,
        uiState = uiState,
        onMarcarTodasLeidasClick = viewModel::marcarTodasLeidas,
        onRetry = viewModel::reintentar,
        onBackClick = onBackClick,
        // Un like lleva al producto de la reseña; un seguidor nuevo, a su perfil
        onNotificacionClick = { notificacion ->
            when (notificacion.type) {
                NotificationType.LIKE ->
                    if (notificacion.productId.isNotBlank()) onProductoClick(notificacion.productId)
                NotificationType.FOLLOW -> onUserClick(notificacion.actorId)
                NotificationType.UNKNOWN -> Unit
            }
        }
    )
}

@Composable
fun NotificationsContent(
    modifier: Modifier = Modifier,
    uiState: NotificationsState,
    onMarcarTodasLeidasClick: () -> Unit,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    onNotificacionClick: (AppNotification) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick,
            trailingContent = {
                Text(
                    text = stringResource(R.string.notifications_mark_all_read),
                    color = Ink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onMarcarTodasLeidasClick() }
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(text = stringResource(R.string.notifications_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            CargaOError(
                isLoading = uiState.isLoading,
                errorMessageRes = uiState.errorMessageRes,
                onRetry = onRetry
            ) {
                if (uiState.notificaciones.isEmpty()) {
                    Text(text = stringResource(R.string.notifications_empty), color = GraySecondary)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.notificaciones, key = { it.id }) { notificacion ->
                            TarjetaNotificacion(
                                notificacion = notificacion,
                                onClick = { onNotificacionClick(notificacion) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "NotificationsScreen - Preview")
@Composable
fun NotificationsScreenPreview() {
    NotificationsContent(
        uiState = NotificationsState(isLoading = false),
        onMarcarTodasLeidasClick = {},
        onRetry = {},
        onBackClick = {},
        onNotificacionClick = {}
    )
}

package com.example.myapplication.ui.screens.Comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.data.Comment
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.MensajeErrorAccion
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Comments.componentes.CampoComentario
import com.example.myapplication.ui.screens.Comments.componentes.DialogoBorrarComentario
import com.example.myapplication.ui.screens.Comments.componentes.TarjetaComentario
import com.example.myapplication.ui.screens.Reviews.componentes.TarjetaResena
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun CommentsScreen(
    reviewId: String,
    modifier: Modifier = Modifier,
    viewModel: CommentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reviewId) { viewModel.cargar(reviewId) }

    MensajeErrorAccion(messageRes = uiState.actionErrorRes, onShown = viewModel::onActionErrorShown)

    if (uiState.comentarioPorBorrar != null) {
        DialogoBorrarComentario(
            onConfirmar = viewModel::confirmarBorrado,
            onCancelar = viewModel::onBorrarCancelado
        )
    }

    CommentsContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::reintentar,
        onUserClick = onUserClick,
        onProductoClick = onProductoClick,
        onLikeClick = viewModel::toggleLike,
        onTextoChange = viewModel::onTextoChange,
        onEnviarClick = viewModel::enviar,
        onBorrarClick = viewModel::onBorrarClick
    )
}

@Composable
fun CommentsContent(
    modifier: Modifier = Modifier,
    uiState: CommentsState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onUserClick: (String) -> Unit,
    onProductoClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    onBorrarClick: (Comment) -> Unit
) {
    val listState = rememberLazyListState()
    var cantidadAnterior by remember { mutableIntStateOf(uiState.comentarios.size) }

    // Cuando llega un comentario nuevo (propio o de otra persona) se baja hasta el final de la conversación
    LaunchedEffect(uiState.comentarios.size) {
        if (uiState.comentarios.size > cantidadAnterior && uiState.comentarios.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
        cantidadAnterior = uiState.comentarios.size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // El teclado empuja la caja de texto; las barras del sistema ya las descuenta el Scaffold
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
    ) {
        BarraSuperior(
            title = stringResource(R.string.comments_title),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = onRetry,
            modifier = Modifier.weight(1f)
        ) {
            val review = uiState.review ?: return@CargaOError
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("commentsList"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "review") {
                        TarjetaResena(
                            resena = review,
                            onAuthorClick = { onUserClick(review.userId) },
                            onLikeClick = onLikeClick,
                            onProductClick = { onProductoClick(review.productId) }
                        )
                    }

                    item(key = "header") {
                        Text(
                            text = stringResource(R.string.comments_header, uiState.comentarios.size),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (uiState.comentarios.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = stringResource(R.string.comments_empty),
                                color = GraySecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }
                    }

                    items(uiState.comentarios, key = { it.id }) { comentario ->
                        TarjetaComentario(
                            comentario = comentario,
                            esAutorDeLaResena = comentario.userId == review.userId,
                            onAutorClick = { onUserClick(comentario.userId) },
                            onBorrarClick = { onBorrarClick(comentario) }
                        )
                    }
                }

                CampoComentario(
                    texto = uiState.texto,
                    errorRes = uiState.textoErrorRes,
                    isSending = uiState.isSending,
                    onTextoChange = onTextoChange,
                    onEnviarClick = onEnviarClick
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "CommentsScreen - Preview")
@Composable
fun CommentsScreenPreview() {
    CommentsContent(
        uiState = CommentsState(
            review = LocalReviewProvider.reviews.first().copy(userId = "u1"),
            isLoading = false,
            comentarios = listOf(
                Comment("c1", "r1", "u2", "Ada Lovelace", null, "¿Te duró bien la batería?", 1L, false, false),
                Comment("c2", "r1", "u1", "Grace Hopper", null, "Sí, casi dos días completos.", 2L, true, true)
            )
        ),
        onBackClick = {},
        onRetry = {},
        onUserClick = {},
        onProductoClick = {},
        onLikeClick = {},
        onTextoChange = {},
        onEnviarClick = {},
        onBorrarClick = {}
    )
}

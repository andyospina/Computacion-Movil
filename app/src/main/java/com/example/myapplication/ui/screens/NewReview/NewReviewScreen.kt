package com.example.myapplication.ui.screens.NewReview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.NewReview.componentes.ContenidoNuevaResena

@Composable
fun NewReviewScreen(
    productId: String,
    reviewId: String,
    modifier: Modifier = Modifier,
    viewModel: NewReviewViewModel = hiltViewModel(),
    onPublicar: (
        productId: String,
        puntuacion: Int,
        esEdicion: Boolean
    ) -> Unit,
    onCancelar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId, reviewId) {
        viewModel.inicializar(productId, reviewId)
    }

    LaunchedEffect(uiState.publicado) {
        if (uiState.publicado) {
            onPublicar(uiState.productId, uiState.puntuacion, uiState.esEdicion)
            viewModel.onPublicado()
        }
    }

    NewReviewContent(
        modifier = modifier,
        uiState = uiState,
        onPuntuacionChange = viewModel::updatePuntuacion,
        onTituloChange = viewModel::updateTitulo,
        onResenaChange = viewModel::updateResena,
        onUsuarioChange = viewModel::updateUsuario,
        onCorreoChange = viewModel::updateCorreo,
        onPublicarClick = viewModel::publicarButtonPress,
        onCancelar = onCancelar
    )
}

@Composable
fun NewReviewContent(
    modifier: Modifier = Modifier,
    uiState: NewReviewState,
    onPuntuacionChange: (Int) -> Unit,
    onTituloChange: (String) -> Unit,
    onResenaChange: (String) -> Unit,
    onUsuarioChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onPublicarClick: () -> Unit,
    onCancelar: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            title = stringResource(
                if (uiState.esEdicion) R.string.new_review_edit_title else R.string.new_review_title
            ),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onCancelar
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            ContenidoNuevaResena(
                modifier = Modifier.fillMaxWidth(),
                puntuacion = uiState.puntuacion,
                titulo = uiState.titulo,
                resena = uiState.resena,
                usuario = uiState.usuario,
                correo = uiState.correo,
                enabled = !uiState.isLoading,
                onPuntuacionChange = onPuntuacionChange,
                onTituloChange = onTituloChange,
                onResenaChange = onResenaChange,
                onUsuarioChange = onUsuarioChange,
                onCorreoChange = onCorreoChange,
                onPublicar = onPublicarClick,
                onCancelar = onCancelar
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            uiState.errorMessageRes?.let { mensajeRes ->
                Text(
                    text = stringResource(mensajeRes),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "NewReviewScreen - Preview")
@Composable
fun NewReviewScreenPreview() {
    NewReviewContent(
        uiState = NewReviewState(),
        onPuntuacionChange = {},
        onTituloChange = {},
        onResenaChange = {},
        onUsuarioChange = {},
        onCorreoChange = {},
        onPublicarClick = {},
        onCancelar = {}
    )
}

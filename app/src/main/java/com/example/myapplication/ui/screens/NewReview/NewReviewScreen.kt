package com.example.myapplication.ui.screens.NewReview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.NewReview.componentes.ContenidoNuevaResena

@Composable
fun NewReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: NewReviewViewModel = viewModel(),
    onPublicar: (
        puntuacion: Int,
        titulo: String,
        resena: String,
        usuario: String
    ) -> Unit,
    onCancelar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.publicado) {
        if (uiState.publicado) {
            onPublicar(uiState.puntuacion, uiState.titulo, uiState.resena, uiState.usuario)
            viewModel.onPublicado()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            title = "Nueva reseña",
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
                onPuntuacionChange = viewModel::updatePuntuacion,
                onTituloChange = viewModel::updateTitulo,
                onResenaChange = viewModel::updateResena,
                onUsuarioChange = viewModel::updateUsuario,
                onCorreoChange = viewModel::updateCorreo,
                onPublicar = viewModel::publicarButtonPress,
                onCancelar = onCancelar
            )
        }
    }
}

@Preview(showBackground = true, name = "NewReviewScreen - Preview")
@Composable
fun NewReviewScreenPreview() {
    NewReviewScreen(
        onPublicar = { _, _, _, _ -> },
        onCancelar = {}
    )
}

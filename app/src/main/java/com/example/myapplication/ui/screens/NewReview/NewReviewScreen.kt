package com.example.myapplication.ui.screens.NewReview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.NewReview.componentes.ContenidoNuevaResena

@Composable
fun NewReviewScreen(
    modifier: Modifier = Modifier,
    onPublicar: (
        puntuacion: Int,
        titulo: String,
        resena: String,
        usuario: String
    ) -> Unit,
    onCancelar: () -> Unit
) {

    var puntuacion by remember {
        mutableIntStateOf(0)
    }

    var titulo by remember {
        mutableStateOf("")
    }

    var resena by remember {
        mutableStateOf("")
    }

    var usuario by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                title = "Nueva reseña",
                navigation = TopBarNavigation.BACK,
                onNavigationClick = onCancelar
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            ContenidoNuevaResena(
                modifier = Modifier.fillMaxWidth(),
                puntuacion = puntuacion,
                titulo = titulo,
                resena = resena,
                usuario = usuario,
                correo = correo,
                onPuntuacionChange = {
                    puntuacion = it
                },
                onTituloChange = {
                    titulo = it
                },
                onResenaChange = {
                    resena = it
                },
                onUsuarioChange = {
                    usuario = it
                },
                onCorreoChange = {
                    correo = it
                },
                onPublicar = {
                    onPublicar(
                        puntuacion,
                        titulo,
                        resena,
                        usuario
                    )
                },
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

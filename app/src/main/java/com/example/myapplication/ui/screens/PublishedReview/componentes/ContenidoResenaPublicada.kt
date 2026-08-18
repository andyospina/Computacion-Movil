package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ContenidoResenaPublicada(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    titulo: String,
    resena: String,
    usuario: String,
    producto: String,
    onVolver: () -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        IconoPublicado(
            modifier = Modifier.size(72.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        TituloPublicado()

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        MensajePublicado(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        TarjetaResenaPublicada(
            modifier = Modifier.fillMaxWidth(),
            puntuacion = puntuacion,
            titulo = titulo,
            resena = resena,
            usuario = usuario,
            producto = producto
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        BotonVolver(
            modifier = Modifier.fillMaxWidth(),
            onClick = onVolver
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "ContenidoResenaPublicada - Preview")
@Composable
fun ContenidoResenaPublicadaPreview() {
    ContenidoResenaPublicada(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        producto = "Grabadora de voz AI",
        onVolver = {}
    )
}
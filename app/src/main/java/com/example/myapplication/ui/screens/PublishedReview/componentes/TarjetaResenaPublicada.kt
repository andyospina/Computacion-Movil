package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaResenaPublicada(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    titulo: String,
    resena: String,
    usuario: String,
    producto: String
) {

    Surface(
        modifier = modifier,
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp)
    ) {

        InformacionResenaPublicada(
            modifier = Modifier.padding(16.dp),
            puntuacion = puntuacion,
            titulo = titulo,
            resena = resena,
            usuario = usuario,
            producto = producto
        )
    }
}

@Preview(showBackground = true, name = "TarjetaResenaPublicada - Preview")
@Composable
fun TarjetaResenaPublicadaPreview() {
    TarjetaResenaPublicada(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        producto = "Grabadora de voz AI"
    )
}
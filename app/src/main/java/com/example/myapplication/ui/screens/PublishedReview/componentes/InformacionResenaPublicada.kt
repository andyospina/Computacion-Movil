package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InformacionResenaPublicada(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    titulo: String,
    resena: String,
    usuario: String,
    producto: String
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = producto,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        EstrellasPublicadas(
            modifier = Modifier,
            puntuacion = puntuacion
        )

        if (titulo.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        if (resena.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = resena,
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }

        if (usuario.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "— $usuario",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, name = "InformacionResenaPublicada - Preview")
@Composable
fun InformacionResenaPublicadaPreview() {
    InformacionResenaPublicada(
        modifier = Modifier.padding(16.dp),
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        producto = "Grabadora de voz AI"
    )
}
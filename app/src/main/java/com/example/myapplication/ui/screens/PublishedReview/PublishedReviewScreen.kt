package com.example.myapplication.ui.screens.PublishedReview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.screens.PublishedReview.componentes.ContenidoResenaPublicada


@Composable
fun PublishedReviewScreen(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    titulo: String,
    resena: String,
    usuario: String,
    producto: String = "Grabadora de voz AI",
    onVolver: () -> Unit
) {

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            BarraSuperior(
                modifier = Modifier.fillMaxWidth()
            )

            ContenidoResenaPublicada(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                puntuacion = puntuacion,
                titulo = titulo,
                resena = resena,
                usuario = usuario,
                producto = producto,
                onVolver = onVolver
            )
        }
    }
}

@Preview(showBackground = true, name = "PublishedReviewScreen - Preview")
@Composable
fun PublishedReviewScreenPreview() {
    PublishedReviewScreen(
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        onVolver = {}
    )
}
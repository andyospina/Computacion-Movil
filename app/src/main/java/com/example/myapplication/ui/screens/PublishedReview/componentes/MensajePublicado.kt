package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MensajePublicado(
    modifier: Modifier = Modifier
) {

    Text(
        text = "Gracias por compartir tu experiencia. " +
                "Tu reseña ayudará a otros compradores " +
                "a decidir mejor.",
        modifier = modifier,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        fontSize = 14.sp
    )
}

@Preview(showBackground = true, name = "MensajePublicado - Preview")
@Composable
fun MensajePublicadoPreview() {
    MensajePublicado(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

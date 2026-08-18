package com.example.myapplication.ui.screens.ConfirmReview

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


@Composable
fun ConfirmReviewScreen(
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

            BarraSuperior()

            ConfirmReviewContent(
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

@Preview(showBackground = true, name = "ConfirmReviewScreen - Preview")
@Composable
fun ConfirmReviewScreenPreview() {
    ConfirmReviewScreen(
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        onVolver = {}
    )
}


@Composable
fun ConfirmReviewContent(
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

        CheckPublicado()

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        MensajePublicado()

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        ResumenResena(
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

        BotonVolverInicio(
            modifier = Modifier.fillMaxWidth(),
            onClick = onVolver
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "ConfirmReviewContent - Preview")
@Composable
fun ConfirmReviewContentPreview() {
    ConfirmReviewContent(
        modifier = Modifier.fillMaxSize(),
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        producto = "Grabadora de voz AI",
        onVolver = {}
    )
}


@Composable
fun CheckPublicado(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .size(72.dp)
            .background(
                color = Color.Black,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "✓",
            color = Color(0xFFD5FF00),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "CheckPublicado - Preview")
@Composable
fun CheckPublicadoPreview() {
    CheckPublicado()
}


@Composable
fun MensajePublicado(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "¡Reseña publicada!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Gracias por compartir tu experiencia. " +
                    "Tu reseña ayudará a otros compradores " +
                    "a decidir mejor.",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true, name = "MensajePublicado - Preview")
@Composable
fun MensajePublicadoPreview() {
    MensajePublicado(
        modifier = Modifier.padding(16.dp)
    )
}


@Composable
fun ResumenResena(
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

        InformacionResena(
            modifier = Modifier.padding(16.dp),
            puntuacion = puntuacion,
            titulo = titulo,
            resena = resena,
            usuario = usuario,
            producto = producto
        )
    }
}

@Preview(showBackground = true, name = "ResumenResena - Preview")
@Composable
fun ResumenResenaPreview() {
    ResumenResena(
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


@Composable
fun InformacionResena(
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

        Text(
            text = "★".repeat(puntuacion) +
                    "☆".repeat(5 - puntuacion),
            color = Color(0xFF809000),
            fontSize = 18.sp
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

@Preview(showBackground = true, name = "InformacionResena - Preview")
@Composable
fun InformacionResenaPreview() {
    InformacionResena(
        modifier = Modifier.padding(16.dp),
        puntuacion = 4,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        producto = "Grabadora de voz AI"
    )
}


@Composable
fun BotonVolverInicio(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD5FF00)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {

        Text(
            text = "Volver al inicio",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "BotonVolverInicio - Preview")
@Composable
fun BotonVolverInicioPreview() {
    BotonVolverInicio(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    )
}
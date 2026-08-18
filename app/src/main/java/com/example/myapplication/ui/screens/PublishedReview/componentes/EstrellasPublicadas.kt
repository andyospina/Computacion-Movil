package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EstrellasPublicadas(
    modifier: Modifier = Modifier,
    puntuacion: Int
) {

    Text(
        text = "★".repeat(puntuacion) +
                "☆".repeat(5 - puntuacion),
        modifier = modifier,
        color = Color(0xFF809000),
        fontSize = 18.sp
    )
}

@Preview(showBackground = true, name = "EstrellasPublicadas - Preview")
@Composable
fun EstrellasPublicadasPreview() {
    EstrellasPublicadas(
        modifier = Modifier.padding(16.dp),
        puntuacion = 4
    )
}


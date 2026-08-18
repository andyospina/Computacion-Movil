package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TituloPublicado(
    modifier: Modifier = Modifier
) {

    Text(
        text = "¡Reseña publicada!",
        modifier = modifier,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true, name = "TituloPublicado - Preview")
@Composable
fun TituloPublicadoPreview() {
    TituloPublicado(
        modifier = Modifier.padding(16.dp)
    )
}
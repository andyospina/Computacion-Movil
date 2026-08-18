package com.example.myapplication.ui.screens.Reviews.componentes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EncabezadoReviews(modifier: Modifier = Modifier) {
    Text(
        text = "Calificaciones y Reseñas (273)",
        modifier = modifier,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true, name = "EncabezadoReviews - Preview")
@Composable
fun EncabezadoReviewsPreview() {
    com.example.myapplication.ui.screens.Reviews.componentes.EncabezadoReviews(
        modifier = Modifier.padding(16.dp)
    )
}
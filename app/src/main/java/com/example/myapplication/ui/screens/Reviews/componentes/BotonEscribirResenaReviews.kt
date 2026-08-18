package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Composable
fun BotonEscribirResenaReviews(modifier: Modifier = Modifier) {
    Button(
        onClick = { /* sin funcionalidad, solo visual */ },
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = "Escribe una Reseña",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "BotonEscribirResenaReviews - Preview")
@Composable
fun BotonEscribirResenaReviewsPreview() {
    BotonEscribirResenaReviews(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
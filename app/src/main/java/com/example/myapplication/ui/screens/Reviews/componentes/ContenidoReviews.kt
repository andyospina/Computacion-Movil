package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun ContenidoReviews(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        EncabezadoReviews(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "RESUMEN",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        ResumenReviews(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        BotonEscribirResenaReviews(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        TituloProductoReviews(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        ResenaEjemplo(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "ContenidoReviews - Preview")
@Composable
fun ContenidoReviewsPreview() {
    ContenidoReviews(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

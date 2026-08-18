package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TituloProductoReviews(modifier: Modifier = Modifier) {
    Text(
        text = "Mira este Producto",
        modifier = modifier,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, name = "TituloProductoReviews - Preview")
@Composable
fun TituloProductoReviewsPreview() {
    com.example.myapplication.ui.screens.Reviews.componentes.TituloProductoReviews(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Product
import com.example.myapplication.ui.components.StarRatingDisplay

@Composable
fun EncabezadoResenas(
    producto: Product,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reseñas (${producto.reviewCount})",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        StarRatingDisplay(rating = Math.round(producto.rating).toInt())
        Text(text = "  ${producto.rating}", fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun EncabezadoResenasPreview() {
    EncabezadoResenas(
        producto = Product(
            id = "1",
            name = "Grabadora de voz AI",
            category = "Tecnología",
            rating = 4.8,
            reviewCount = 273,
            recommendPercent = 88,
            ratingBreakdown = emptyList()
        ),
        modifier = Modifier.padding(16.dp)
    )
}

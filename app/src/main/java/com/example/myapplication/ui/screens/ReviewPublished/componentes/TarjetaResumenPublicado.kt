package com.example.myapplication.ui.screens.ReviewPublished.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun TarjetaResumenPublicado(
    producto: Product,
    calificacion: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = BorderColor.copy(alpha = 0.25f), shape = RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = BorderColor.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
        )

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = producto.name, fontWeight = FontWeight.Bold)

            Row {
                StarRatingDisplay(rating = calificacion, fontSize = 14.sp)
                Text(text = "  Tu calificación", color = GraySecondary, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaResumenPublicadoPreview() {
    TarjetaResumenPublicado(
        producto = Product(
            id = "1",
            name = "Grabadora de voz AI",
            category = "Tecnología",
            rating = 4.8,
            reviewCount = 273,
            recommendPercent = 88,
            ratingBreakdown = emptyList()
        ),
        calificacion = 4,
        modifier = Modifier.padding(16.dp)
    )
}

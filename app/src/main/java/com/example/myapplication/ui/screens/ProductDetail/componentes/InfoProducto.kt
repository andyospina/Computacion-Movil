package com.example.myapplication.ui.screens.ProductDetail.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Product
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun InfoProducto(
    producto: Product,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = producto.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StarRatingDisplay(rating = Math.round(producto.rating).toInt())
            Text(
                text = stringResource(
                    R.string.product_info_summary,
                    producto.rating.toString(),
                    producto.reviewCount,
                    producto.recommendPercent
                ),
                color = GraySecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoProductoPreview() {
    InfoProducto(
        producto = Product(
            id = "1",
            name = "Grabadora de voz AI",
            category = "Tecnología",
            rating = 4.8,
            reviewCount = 273,
            recommendPercent = 88,
            ratingBreakdown = emptyList()
        )
    )
}

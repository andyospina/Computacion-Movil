package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Product
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = BorderColor.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingDisplay(rating = Math.round(product.rating).toInt(), fontSize = 13.sp)
                Text(
                    text = stringResource(
                        R.string.product_list_item_summary,
                        product.rating.toString(),
                        product.reviewCount
                    ),
                    fontSize = 12.sp,
                    color = GraySecondary
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GraySecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListItemPreview() {
    ProductListItem(
        product = Product(
            id = "1",
            name = "Grabadora de voz AI",
            category = "Tecnología",
            rating = 4.8,
            reviewCount = 273,
            recommendPercent = 88,
            ratingBreakdown = emptyList()
        ),
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

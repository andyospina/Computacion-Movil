package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun StarRatingDisplay(
    rating: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    Row(modifier = modifier) {
        Text(
            text = "★".repeat(rating.coerceIn(0, maxStars)),
            color = DeepLime,
            fontSize = fontSize
        )
        Text(
            text = "☆".repeat((maxStars - rating).coerceIn(0, maxStars)),
            color = GraySecondary,
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StarRatingDisplayPreview() {
    StarRatingDisplay(rating = 4)
}

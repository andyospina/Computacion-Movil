package com.example.myapplication.ui.screens.PublishedReview.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
fun IconoPublicado(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "✓",
            color = Color(0xFFD5FF00),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "IconoPublicado - Preview")
@Composable
fun IconoPublicadoPreview() {
    IconoPublicado(
        modifier = Modifier.size(72.dp)
    )
}
package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectorEstrellas(
    puntuacion: Int,
    onPuntuacionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.padding(vertical = 8.dp)
    ) {

        for (i in 1..5) {

            Text(
                text = if (i <= puntuacion) "★" else "☆",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = if (i <= puntuacion) {
                    Color(0xFF809000)
                } else {
                    Color.Gray
                },
                modifier = Modifier
                    .clickable {
                        onPuntuacionChange(i)
                    }
            )
        }
    }
}

@Composable
@Preview (showBackground = true)
fun SelectorEstrellasPreview() {
    SelectorEstrellas(
        puntuacion = 3,
        onPuntuacionChange = {}
    )
}
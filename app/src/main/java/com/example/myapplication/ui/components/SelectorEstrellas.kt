package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

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
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (i <= puntuacion) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.outline
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
@Preview(showBackground = true)
fun SelectorEstrellasPreview() {
    MyApplicationTheme(darkTheme = false) {
        SelectorEstrellas(
            puntuacion = 3,
            onPuntuacionChange = {}
        )
    }
}

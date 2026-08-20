package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.SelectorEstrellas
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CampoPuntuacion(
    puntuacion: Int,
    onPuntuacionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Score:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SelectorEstrellas(
            puntuacion = puntuacion,
            onPuntuacionChange = onPuntuacionChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoPuntuacionPreview() {
    MyApplicationTheme(darkTheme = false) {
        CampoPuntuacion(puntuacion = 4, onPuntuacionChange = {})
    }
}

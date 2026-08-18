package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.SelectorEstrellas

@Composable
fun CampoPuntuacion(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    onPuntuacionChange: (Int) -> Unit
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = "Puntuación:"
        )

        SelectorEstrellas(
            puntuacion = puntuacion,
            onPuntuacionChange = onPuntuacionChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "CampoPuntuacion - Preview")
@Composable
fun CampoPuntuacionPreview() {
    CampoPuntuacion(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        puntuacion = 4,
        onPuntuacionChange = {}
    )
}
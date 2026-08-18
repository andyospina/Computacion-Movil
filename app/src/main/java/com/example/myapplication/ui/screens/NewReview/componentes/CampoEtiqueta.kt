package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CampoEtiquetas(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = "Opción 1:",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Etiqueta("Light")
            Etiqueta("Fair")
            Etiqueta("Medium")
            Etiqueta("Dark")
        }
    }
}

@Preview(showBackground = true, name = "CampoEtiquetas - Preview")
@Composable
fun CampoEtiquetasPreview() {
    CampoEtiquetas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
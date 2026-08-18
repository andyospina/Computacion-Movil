package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun Etiqueta(
    texto: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = {},
        modifier = modifier
    ) {
        Text(
            text = texto,
            fontSize = 12.sp
        )
    }
}
@Composable
@Preview(showBackground = true)
fun EtiquetaPreview(){
    Etiqueta("Utíl")
}
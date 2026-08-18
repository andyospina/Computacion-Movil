package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CampoTitulo(
    modifier: Modifier = Modifier,
    titulo: String,
    onTituloChange: (String) -> Unit
) {

    OutlinedTextField(
        value = titulo,
        onValueChange = onTituloChange,
        label = {
            Text("Título")
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "CampoTitulo - Preview")
@Composable
fun CampoTituloPreview() {
    CampoTitulo(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        titulo = "Muy buena calidad de audio",
        onTituloChange = {}
    )
}


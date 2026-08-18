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
fun CampoCorreo(
    modifier: Modifier = Modifier,
    correo: String,
    onCorreoChange: (String) -> Unit
) {

    OutlinedTextField(
        value = correo,
        onValueChange = onCorreoChange,
        label = {
            Text("*Correo")
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "CampoCorreo - Preview")
@Composable
fun CampoCorreoPreview() {
    CampoCorreo(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        correo = "camila@example.com",
        onCorreoChange = {}
    )
}
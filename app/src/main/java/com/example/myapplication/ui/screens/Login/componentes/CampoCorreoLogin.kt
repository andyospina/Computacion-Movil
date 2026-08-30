package com.example.myapplication.ui.screens.Login.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CampoCorreoLogin(
    modifier: Modifier = Modifier,
    correo: String,
    onCorreoChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "Correo:", fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = correo,
            onValueChange = onCorreoChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoCorreoLoginPreview() {
    CampoCorreoLogin(
        modifier = Modifier.padding(16.dp),
        correo = "emily@correo.com",
        onCorreoChange = {}
    )
}

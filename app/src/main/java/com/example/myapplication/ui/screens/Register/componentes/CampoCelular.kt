package com.example.myapplication.ui.screens.Register.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CampoCelular(
    modifier: Modifier = Modifier,
    celular: String,
    onCelularChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "Celular:", fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = celular,
            onValueChange = onCelularChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoCelularPreview() {
    CampoCelular(
        modifier = Modifier.padding(16.dp),
        celular = "3001234567",
        onCelularChange = {}
    )
}

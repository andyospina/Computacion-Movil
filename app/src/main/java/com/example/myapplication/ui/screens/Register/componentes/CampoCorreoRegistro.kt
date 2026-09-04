package com.example.myapplication.ui.screens.Register.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun CampoCorreoRegistro(
    modifier: Modifier = Modifier,
    correo: String,
    onCorreoChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.label_email), fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = correo,
            onValueChange = onCorreoChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoCorreoRegistroPreview() {
    CampoCorreoRegistro(
        modifier = Modifier.padding(16.dp),
        correo = "emily@correo.com",
        onCorreoChange = {}
    )
}

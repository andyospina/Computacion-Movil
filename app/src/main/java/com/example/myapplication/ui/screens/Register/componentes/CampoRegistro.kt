package com.example.myapplication.ui.screens.Register.componentes

import androidx.annotation.StringRes
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

/** Campo de texto genérico del formulario de registro (nombre, usuario, país, biografía). */
@Composable
fun CampoRegistro(
    modifier: Modifier = Modifier,
    @StringRes etiqueta: Int,
    valor: String,
    onValorChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        Text(text = stringResource(etiqueta), fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoRegistroPreview() {
    CampoRegistro(
        modifier = Modifier.padding(16.dp),
        etiqueta = R.string.label_name,
        valor = "Ada Lovelace",
        onValorChange = {}
    )
}

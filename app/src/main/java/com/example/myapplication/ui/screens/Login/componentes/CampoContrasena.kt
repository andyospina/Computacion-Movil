package com.example.myapplication.ui.screens.Login.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.DeepLime

@Composable
fun CampoContrasena(
    modifier: Modifier = Modifier,
    contrasena: String,
    visible: Boolean,
    onContrasenaChange: (String) -> Unit,
    onVisibleChange: (Boolean) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.label_password), fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = contrasena,
            onValueChange = onContrasenaChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { onVisibleChange(!visible) }) {
                    Text(
                        text = stringResource(
                            if (visible) R.string.password_visibility_hide else R.string.password_visibility_show
                        ),
                        color = DeepLime
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampoContrasenaPreview() {
    CampoContrasena(
        modifier = Modifier.padding(16.dp),
        contrasena = "12345678",
        visible = false,
        onContrasenaChange = {},
        onVisibleChange = {}
    )
}

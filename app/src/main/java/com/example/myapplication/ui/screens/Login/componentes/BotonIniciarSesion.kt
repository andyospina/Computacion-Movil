package com.example.myapplication.ui.screens.Login.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun BotonIniciarSesion(
    modifier: Modifier = Modifier,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricLime,
            contentColor = Ink
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = stringResource(R.string.login_button), fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun BotonIniciarSesionPreview() {
    BotonIniciarSesion(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        habilitado = true,
        onClick = {}
    )
}

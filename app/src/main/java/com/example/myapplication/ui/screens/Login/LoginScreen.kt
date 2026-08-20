package com.example.myapplication.ui.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.screens.Login.componentes.CabeceraLogin
import com.example.myapplication.ui.screens.Login.componentes.FormularioLogin
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onIniciarSesion: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraLogin()

        FormularioLogin(
            modifier = Modifier.padding(24.dp),
            onIniciarSesion = onIniciarSesion
        )
    }
}

@Preview(showBackground = true, name = "LoginScreen - Preview")
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        LoginScreen(onIniciarSesion = {})
    }
}

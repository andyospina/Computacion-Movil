package com.example.myapplication.ui.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.AppLogo
import com.example.myapplication.ui.screens.Login.componentes.BotonIniciarSesion
import com.example.myapplication.ui.screens.Login.componentes.CampoContrasena
import com.example.myapplication.ui.screens.Login.componentes.CampoCorreoLogin
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onIniciarSesionClick: () -> Unit,
    onRegistrateClick: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ink)
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo(size = 80.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ReviewLab",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Opiniones reales de compradores reales",
                    color = GraySecondary,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top
            ) {

                CampoCorreoLogin(
                    modifier = Modifier.fillMaxWidth(),
                    correo = correo,
                    onCorreoChange = { correo = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CampoContrasena(
                    modifier = Modifier.fillMaxWidth(),
                    contrasena = contrasena,
                    visible = contrasenaVisible,
                    onContrasenaChange = { contrasena = it },
                    onVisibleChange = { contrasenaVisible = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = DeepLime,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(16.dp))

                BotonIniciarSesion(
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = correo.isNotBlank() && contrasena.isNotBlank(),
                    onClick = onIniciarSesionClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(text = "  o  ", color = GraySecondary)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onIniciarSesionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(text = "Continuar con Google", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "¿No tienes cuenta? ", color = GraySecondary)
                    Text(
                        text = "Regístrate",
                        color = DeepLime,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRegistrateClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "LoginScreen - Preview")
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onIniciarSesionClick = {},
        onRegistrateClick = {}
    )
}

package com.example.myapplication.ui.screens.Register

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.AppLogo
import com.example.myapplication.ui.screens.Login.componentes.CampoContrasena
import com.example.myapplication.ui.screens.Register.componentes.BotonRegistrarse
import com.example.myapplication.ui.screens.Register.componentes.CampoCelular
import com.example.myapplication.ui.screens.Register.componentes.CampoCorreoRegistro
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(),
    onRegistroExitoso: () -> Unit,
    onIniciarSesionClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigate) {
        if (uiState.navigate) {
            onRegistroExitoso()
            viewModel.onNavigated()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
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
                text = "Crea tu cuenta",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a la comunidad de ReviewLab",
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

            CampoCorreoRegistro(
                modifier = Modifier.fillMaxWidth(),
                correo = uiState.email,
                onCorreoChange = viewModel::updateEmail
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoCelular(
                modifier = Modifier.fillMaxWidth(),
                celular = uiState.cellphone,
                onCelularChange = viewModel::updateCellphone
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoContrasena(
                modifier = Modifier.fillMaxWidth(),
                contrasena = uiState.password,
                visible = uiState.showPassword,
                onContrasenaChange = viewModel::updatePassword,
                onVisibleChange = { viewModel.togglePasswordVisibility() }
            )

            if (uiState.showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonRegistrarse(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::registerButtonPress
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes cuenta? ", color = GraySecondary)
                Text(
                    text = "Inicia sesión",
                    color = DeepLime,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onIniciarSesionClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "RegisterScreen - Preview")
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegistroExitoso = {},
        onIniciarSesionClick = {}
    )
}

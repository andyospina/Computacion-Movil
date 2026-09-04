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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
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
    viewModel: LoginViewModel = viewModel(),
    onIniciarSesionClick: () -> Unit,
    onRegistrateClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigate) {
        if (uiState.navigate) {
            onIniciarSesionClick()
            viewModel.onNavigated()
        }
    }

    LoginContent(
        modifier = modifier,
        uiState = uiState,
        onCorreoChange = viewModel::updateEmail,
        onContrasenaChange = viewModel::updatePassword,
        onVisibleChange = { viewModel.togglePasswordVisibility() },
        onLoginClick = viewModel::loginButtonPress,
        onRegistrateClick = onRegistrateClick
    )
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    uiState: LoginState,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onVisibleChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onRegistrateClick: () -> Unit
) {
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
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.login_tagline),
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
                correo = uiState.email,
                onCorreoChange = onCorreoChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoContrasena(
                modifier = Modifier.fillMaxWidth(),
                contrasena = uiState.password,
                visible = uiState.showPassword,
                onContrasenaChange = onContrasenaChange,
                onVisibleChange = onVisibleChange
            )

            if (uiState.showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(uiState.errorMessageRes),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.login_forgot_password),
                color = DeepLime,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotonIniciarSesion(
                modifier = Modifier.fillMaxWidth(),
                habilitado = uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(text = stringResource(R.string.divider_or), color = GraySecondary)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = stringResource(R.string.login_continue_with_google), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.login_no_account_prompt), color = GraySecondary)
                Text(
                    text = stringResource(R.string.login_register_link),
                    color = DeepLime,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegistrateClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "LoginScreen - Preview")
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginState(),
        onCorreoChange = {},
        onContrasenaChange = {},
        onVisibleChange = {},
        onLoginClick = {},
        onRegistrateClick = {}
    )
}

package com.example.myapplication.ui.screens.Profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.ui.screens.Profile.componentes.EncabezadoPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EstadisticasPerfil
import com.example.myapplication.ui.screens.Profile.componentes.SelectorTema
import com.example.myapplication.ui.screens.Profile.componentes.TarjetaMiResena
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Photo Picker del sistema: no requiere permisos de almacenamiento
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = viewModel::onImageSelected
    )

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onLoggedOut()
            viewModel.onLoggedOut()
        }
    }

    ProfileContent(
        modifier = modifier,
        uiState = uiState,
        modoOscuro = modoOscuro,
        onModoOscuroChange = onModoOscuroChange,
        onSelectImageClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onLogoutClick = viewModel::logoutButtonPress
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: ProfileState,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onSelectImageClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        uiState.user?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ink)
                    .padding(20.dp)
            ) {
                EncabezadoPerfil(
                    user = user,
                    profileImageUrl = uiState.profileImageUrl,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = uiState.email, color = GraySecondary)
                }
            }

            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                EstadisticasPerfil(user = user, modifier = Modifier.fillMaxWidth())
            }
        }

        OutlinedButton(
            onClick = onSelectImageClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(
                    if (uiState.isLoading) R.string.profile_uploading_image
                    else R.string.profile_select_image_button
                )
            )
        }

        uiState.errorMessageRes?.let { mensajeRes ->
            Text(
                text = stringResource(mensajeRes),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = stringResource(R.string.profile_logout_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        SelectorTema(
            modoOscuro = modoOscuro,
            onModoOscuroChange = onModoOscuroChange,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.profile_my_reviews_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.misResenas, key = { it.id }) { resena ->
                val producto = LocalProductProvider.findById(resena.productId)
                TarjetaMiResena(resena = resena, nombreProducto = producto.name)
            }
        }
    }
}

@Preview(showBackground = true, name = "ProfileScreen - Preview")
@Composable
fun ProfileScreenPreview() {
    ProfileContent(
        uiState = ProfileState(),
        modoOscuro = false,
        onModoOscuroChange = {},
        onSelectImageClick = {},
        onLogoutClick = {}
    )
}

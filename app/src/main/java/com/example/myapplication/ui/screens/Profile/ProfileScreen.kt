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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.screens.Profile.componentes.DialogoEditarPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EncabezadoPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EstadisticasPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EstadisticasSeguidores
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
    onEditResena: (productId: String, reviewId: String) -> Unit,
    onFollowersClick: (userId: String) -> Unit,
    onFollowingClick: (userId: String) -> Unit,
    onSavedClick: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Se refresca al volver a la pantalla (p. ej. tras editar una reseña)
    LaunchedEffect(Unit) { viewModel.getPerfil() }

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
        onLogoutClick = viewModel::logoutButtonPress,
        onSaveProfile = viewModel::updateProfile,
        onProfileSaved = viewModel::onProfileSaved,
        onEditDismissed = viewModel::onEditDismissed,
        onRetry = viewModel::getPerfil,
        onEditResena = onEditResena,
        onDeleteResena = viewModel::deleteResena,
        onFollowersClick = { onFollowersClick(uiState.userId) },
        onFollowingClick = { onFollowingClick(uiState.userId) },
        onSavedClick = onSavedClick
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: ProfileState,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onSelectImageClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSaveProfile: (name: String, country: String, bio: String) -> Unit,
    onProfileSaved: () -> Unit,
    onEditDismissed: () -> Unit,
    onRetry: () -> Unit,
    onEditResena: (productId: String, reviewId: String) -> Unit,
    onDeleteResena: (reviewId: String) -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onSavedClick: () -> Unit = {}
) {
    var mostrarEdicion by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.profileSaved) {
        if (uiState.profileSaved) {
            mostrarEdicion = false
            onProfileSaved()
        }
    }

    if (mostrarEdicion) {
        uiState.user?.let { user ->
            DialogoEditarPerfil(
                user = user,
                isSaving = uiState.isSavingProfile,
                errorRes = uiState.profileSaveErrorRes,
                onGuardar = onSaveProfile,
                onDismiss = {
                    mostrarEdicion = false
                    onEditDismissed()
                }
            )
        }
    }

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

                Spacer(modifier = Modifier.height(8.dp))

                EstadisticasSeguidores(
                    followersCount = uiState.followStats.followersCount,
                    followingCount = uiState.followStats.followingCount,
                    onFollowersClick = onFollowersClick,
                    onFollowingClick = onFollowingClick
                )
            }

            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                EstadisticasPerfil(user = user, modifier = Modifier.fillMaxWidth())
            }
        }

        OutlinedButton(
            onClick = { mostrarEdicion = true },
            enabled = uiState.user != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = stringResource(R.string.profile_edit_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSavedClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = stringResource(R.string.profile_saved_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        CargaOError(
            isLoading = uiState.isLoadingData,
            errorMessageRes = uiState.dataErrorRes,
            onRetry = onRetry
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.misResenas, key = { it.id }) { resena ->
                    TarjetaMiResena(
                        resena = resena,
                        nombreProducto = resena.productName,
                        onEditClick = { onEditResena(resena.productId, resena.id) },
                        onDeleteClick = { onDeleteResena(resena.id) }
                    )
                }
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
        onLogoutClick = {},
        onSaveProfile = { _, _, _ -> },
        onProfileSaved = {},
        onEditDismissed = {},
        onRetry = {},
        onEditResena = { _, _ -> },
        onDeleteResena = {},
        onFollowersClick = {},
        onFollowingClick = {}
    )
}

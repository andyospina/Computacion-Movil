package com.example.myapplication.ui.screens.UserProfile

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
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.Profile.componentes.EncabezadoPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EstadisticasPerfil
import com.example.myapplication.ui.screens.Profile.componentes.TarjetaMiResena
import com.example.myapplication.ui.theme.Ink

@Composable
fun UserProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) { viewModel.getPerfil(userId) }

    UserProfileContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = viewModel::reintentar,
        onBackClick = onBackClick
    )
}

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    uiState: UserProfileState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {

        BarraSuperior(
            title = uiState.user?.name ?: stringResource(R.string.app_name),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = onRetry
        ) {
            uiState.user?.let { user ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Ink)
                            .padding(20.dp)
                    ) {
                        EncabezadoPerfil(
                            user = user,
                            profileImageUrl = user.profileImage,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        EstadisticasPerfil(user = user, modifier = Modifier.fillMaxWidth())
                    }

                    Text(
                        text = stringResource(R.string.user_profile_reviews_title),
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
                        items(uiState.resenas, key = { it.id }) { resena ->
                            TarjetaMiResena(resena = resena, nombreProducto = resena.productName)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "UserProfileScreen - Preview")
@Composable
fun UserProfileScreenPreview() {
    UserProfileContent(uiState = UserProfileState(), onRetry = {}, onBackClick = {})
}

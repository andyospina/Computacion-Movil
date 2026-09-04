package com.example.myapplication.ui.screens.Home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.InitialsAvatar
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.screens.Home.componentes.BarraBusquedaProductos
import com.example.myapplication.ui.screens.Home.componentes.ChipsCategorias
import com.example.myapplication.ui.screens.Home.componentes.ListaProductosTendencia

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onSearchBarClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        modifier = modifier,
        uiState = uiState,
        onCategoriaChange = viewModel::updateCategoria,
        onSearchBarClick = onSearchBarClick,
        onProductoClick = onProductoClick,
        onAvatarClick = onAvatarClick,
        onNotificationsClick = onNotificationsClick
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeState,
    onCategoriaChange: (String) -> Unit,
    onSearchBarClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.MENU,
            trailingContent = {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.content_description_notifications),
                        tint = Ink,
                        modifier = Modifier.clickable { onNotificationsClick() }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    InitialsAvatar(
                        initials = LocalUserProvider.currentUser.initials,
                        modifier = Modifier.clickable { onAvatarClick() }
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            BarraBusquedaProductos(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSearchBarClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChipsCategorias(
                categoriaSeleccionada = uiState.categoriaSeleccionada,
                onCategoriaChange = onCategoriaChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.home_trending_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ListaProductosTendencia(
                productos = uiState.productos,
                onProductoClick = onProductoClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Preview")
@Composable
fun HomeScreenPreview() {
    HomeContent(
        uiState = HomeState(),
        onCategoriaChange = {},
        onSearchBarClick = {},
        onProductoClick = {},
        onAvatarClick = {},
        onNotificationsClick = {}
    )
}

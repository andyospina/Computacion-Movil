package com.example.myapplication.ui.screens.Home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.ProductListItem
import com.example.myapplication.ui.components.ProfileAsyncImage
import com.example.myapplication.ui.components.SolicitarPermisoNotificaciones
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.screens.Home.componentes.BarraBusquedaProductos
import com.example.myapplication.ui.screens.Home.componentes.ChipsCategorias

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchBarClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Primera pantalla con sesión iniciada: aquí se pide el permiso de notificaciones
    SolicitarPermisoNotificaciones()

    // Se refresca al volver a la pantalla por si la foto cambió en Perfil
    LaunchedEffect(Unit) {
        viewModel.getProfileImage()
        viewModel.getProductos()
    }

    HomeContent(
        modifier = modifier,
        uiState = uiState,
        onCategoriaChange = viewModel::updateCategoria,
        onRetry = viewModel::getProductos,
        onSearchBarClick = onSearchBarClick,
        onProductoClick = onProductoClick,
        onAvatarClick = onAvatarClick,
        onNotificationsClick = onNotificationsClick,
        onMapClick = onMapClick
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeState,
    onCategoriaChange: (String) -> Unit,
    onRetry: () -> Unit,
    onSearchBarClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().testTag("homeScreen")
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.MENU,
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = stringResource(R.string.content_description_map),
                        tint = Ink,
                        modifier = Modifier.clickable { onMapClick() }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.content_description_notifications),
                        tint = Ink,
                        modifier = Modifier.clickable { onNotificationsClick() }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    ProfileAsyncImage(
                        profileImage = uiState.profileImageUrl,
                        size = 36.dp,
                        modifier = Modifier.clickable { onAvatarClick() }
                    )
                }
            }
        )

        // Una sola lista con scroll: en horizontal o con letra grande el buscador, las categorías y el
        // título dejaban la lista de tendencias sin espacio.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "search") {
                BarraBusquedaProductos(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSearchBarClick
                )
            }

            item(key = "chips") {
                ChipsCategorias(
                    categoriaSeleccionada = uiState.categoriaSeleccionada,
                    onCategoriaChange = onCategoriaChange
                )
            }

            item(key = "title") {
                Text(
                    text = stringResource(R.string.home_trending_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            when {
                uiState.isLoading -> item(key = "loading") {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessageRes != null -> item(key = "error") {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(uiState.errorMessageRes),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        OutlinedButton(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
                            Text(text = stringResource(R.string.action_retry))
                        }
                    }
                }

                else -> items(uiState.productos, key = { it.id }) { producto ->
                    ProductListItem(
                        product = producto,
                        onClick = { onProductoClick(producto.id) },
                        modifier = Modifier.testTag("productItem")
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Preview")
@Composable
fun HomeScreenPreview() {
    HomeContent(
        uiState = HomeState(),
        onCategoriaChange = {},
        onRetry = {},
        onSearchBarClick = {},
        onProductoClick = {},
        onAvatarClick = {},
        onNotificationsClick = {},
        onMapClick = {}
    )
}

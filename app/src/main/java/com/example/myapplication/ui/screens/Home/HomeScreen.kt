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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.LocalProductProvider
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
    onSearchBarClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todo") }

    val productos = remember(categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todo") {
            LocalProductProvider.trending()
        } else {
            LocalProductProvider.products.filter { it.category == categoriaSeleccionada }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                navigation = TopBarNavigation.MENU,
                trailingContent = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            BarraBusquedaProductos(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSearchBarClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChipsCategorias(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaChange = { categoriaSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tendencias esta semana",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ListaProductosTendencia(
                productos = productos,
                onProductoClick = onProductoClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Preview")
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onSearchBarClick = {},
        onProductoClick = {},
        onAvatarClick = {},
        onNotificationsClick = {}
    )
}

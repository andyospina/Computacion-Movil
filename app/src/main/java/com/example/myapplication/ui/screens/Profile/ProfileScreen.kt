package com.example.myapplication.ui.screens.Profile

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.LocalProductProvider
import com.example.myapplication.ui.screens.Profile.componentes.EncabezadoPerfil
import com.example.myapplication.ui.screens.Profile.componentes.EstadisticasPerfil
import com.example.myapplication.ui.screens.Profile.componentes.SelectorTema
import com.example.myapplication.ui.screens.Profile.componentes.TarjetaMiResena
import com.example.myapplication.ui.theme.Ink

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                EstadisticasPerfil(user = user, modifier = Modifier.fillMaxWidth())
            }
        }

        SelectorTema(
            modoOscuro = modoOscuro,
            onModoOscuroChange = onModoOscuroChange,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mis reseñas",
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
    ProfileScreen(
        modoOscuro = false,
        onModoOscuroChange = {}
    )
}

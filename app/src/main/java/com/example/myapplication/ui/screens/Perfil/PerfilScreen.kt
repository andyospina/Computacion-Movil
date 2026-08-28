package com.example.myapplication.ui.screens.Perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Perfil
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.screens.Perfil.componentes.CabeceraPerfil
import com.example.myapplication.ui.screens.Perfil.componentes.EstadisticasPerfil
import com.example.myapplication.ui.screens.Perfil.componentes.FilaModoOscuro
import com.example.myapplication.ui.screens.Perfil.componentes.TarjetaMiResena
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    perfil: Perfil,
    misResenas: List<Resena>,
    productos: List<Producto>,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraPerfil(perfil = perfil)

        EstadisticasPerfil(perfil = perfil)

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Mis reseñas", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            misResenas.forEach { resena ->
                val producto = productos.firstOrNull { it.id == resena.productoId }
                if (producto != null) {
                    TarjetaMiResena(resena = resena, producto = producto)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            FilaModoOscuro(
                activo = modoOscuro,
                onActivoChange = onModoOscuroChange,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "PerfilScreen - Preview")
@Composable
fun PerfilScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        PerfilScreen(
            perfil = DatosLocales.perfilUsuario,
            misResenas = DatosLocales.misResenas,
            productos = DatosLocales.productos,
            modoOscuro = false,
            onModoOscuroChange = {}
        )
    }
}

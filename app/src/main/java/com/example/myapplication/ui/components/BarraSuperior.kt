package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

enum class IconoInicial { HAMBURGUESA, VOLVER, NINGUNO }

/**
 * Único componente de barra superior de la app (topBar del único Scaffold,
 * en ReviewLabApp / MainActivity.kt). Cada pantalla configura su icono
 * inicial y su contenido final para igualar el diseño; Login y Perfil no
 * usan esta barra porque tienen su propia cabecera oscura.
 */
@Composable
fun BarraSuperior(
    modifier: Modifier = Modifier,
    titulo: String = "ReviewLab",
    iconoInicial: IconoInicial = IconoInicial.HAMBURGUESA,
    onVolverClick: () -> Unit = {},
    contenidoFinal: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (iconoInicial) {
            IconoInicial.HAMBURGUESA -> IconoHamburguesa()
            IconoInicial.VOLVER -> Text(
                text = "‹",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable { onVolverClick() }
            )
            IconoInicial.NINGUNO -> {}
        }

        if (iconoInicial != IconoInicial.NINGUNO) {
            Spacer(modifier = Modifier.width(14.dp))
        }

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )

        contenidoFinal()
    }
}

@Composable
private fun IconoHamburguesa() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(width = 20.dp, height = 2.5.dp)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }
    }
}

@Composable
fun AvatarIniciales(
    iniciales: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 28.dp
) {
    Box(
        modifier = modifier
            .size(tamano)
            .background(MaterialTheme.colorScheme.onPrimaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
@Preview
fun BarraSuperiorPreview() {
    MyApplicationTheme(darkTheme = false) {
        Column {
            BarraSuperior(titulo = "ReviewLab") {
                Text("Login", fontWeight = FontWeight.Bold)
            }
            BarraSuperior(titulo = "ReviewLab", iconoInicial = IconoInicial.VOLVER) {
                Text("⋮")
            }
            BarraSuperior(titulo = "ReviewLab") {
                AvatarIniciales("EM")
            }
        }
    }
}

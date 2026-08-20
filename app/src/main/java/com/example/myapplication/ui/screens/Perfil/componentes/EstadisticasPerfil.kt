package com.example.myapplication.ui.screens.Perfil.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Perfil
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun EstadisticasPerfil(
    perfil: Perfil,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight)
    ) {
        Dato(valor = perfil.totalResenas.toString(), etiqueta = "Reseñas", modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(BorderLight)
        )
        Dato(valor = perfil.votosUtiles.toString(), etiqueta = "Votos útiles", modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(BorderLight)
        )
        Dato(
            valor = "${perfil.promedio}★",
            etiqueta = "Promedio",
            modifier = Modifier.weight(1f),
            colorValor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun Dato(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
    colorValor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorValor)
        Text(text = etiqueta, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun EstadisticasPerfilPreview() {
    MyApplicationTheme(darkTheme = false) {
        EstadisticasPerfil(perfil = DatosLocales.perfilUsuario)
    }
}

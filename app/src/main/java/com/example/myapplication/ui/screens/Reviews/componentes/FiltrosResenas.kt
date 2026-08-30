package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BorderColor

enum class OrdenResenas { RECIENTES, MEJOR_VALORADAS }

@Composable
fun FiltrosResenas(
    orden: OrdenResenas,
    conFotos: Boolean,
    onOrdenChange: (OrdenResenas) -> Unit,
    onConFotosChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroTab(
                texto = "Recientes",
                seleccionado = orden == OrdenResenas.RECIENTES,
                onClick = { onOrdenChange(OrdenResenas.RECIENTES) }
            )
            FiltroTab(
                texto = "Mejor valoradas",
                seleccionado = orden == OrdenResenas.MEJOR_VALORADAS,
                onClick = { onOrdenChange(OrdenResenas.MEJOR_VALORADAS) }
            )
        }

        Row(modifier = Modifier.padding(top = 8.dp)) {
            FiltroTab(
                texto = "Con fotos",
                seleccionado = conFotos,
                onClick = { onConFotosChange(!conFotos) }
            )
        }
    }
}

@Composable
private fun FiltroTab(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val selectedBackground = MaterialTheme.colorScheme.onBackground
    val selectedText = MaterialTheme.colorScheme.background

    Text(
        text = texto,
        color = if (seleccionado) selectedText else MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(
                color = if (seleccionado) selectedBackground else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .border(width = 1.dp, color = if (seleccionado) selectedBackground else BorderColor, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun FiltrosResenasPreview() {
    FiltrosResenas(
        orden = OrdenResenas.RECIENTES,
        conFotos = false,
        onOrdenChange = {},
        onConFotosChange = {},
        modifier = Modifier.padding(16.dp)
    )
}

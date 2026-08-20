package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Chip de filtro en forma de píldora: seleccionado = fondo Ink, texto lima;
 * no seleccionado = borde gris, texto normal. Reutilizado en Reviews
 * (Recientes/Mejor valoradas/Con fotos), Home (categorías) y Buscar.
 */
@Composable
fun ChipFiltro(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .then(
                if (seleccionado) {
                    Modifier.background(MaterialTheme.colorScheme.onSurface)
                } else {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape)
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipFiltroPreview() {
    MyApplicationTheme(darkTheme = false) {
        Row {
            ChipFiltro(texto = "Recientes", seleccionado = true, onClick = {})
            ChipFiltro(texto = "Mejor valoradas", seleccionado = false, onClick = {})
        }
    }
}

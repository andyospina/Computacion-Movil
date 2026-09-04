package com.example.myapplication.ui.screens.Home.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun ChipsCategorias(
    categoriaSeleccionada: String,
    onCategoriaChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categorias = stringArrayResource(R.array.categorias).toList()

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { categoria ->
            val seleccionada = categoria == categoriaSeleccionada

            Text(
                text = categoria,
                color = if (seleccionada) Ink else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .background(
                        color = if (seleccionada) ElectricLime else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (seleccionada) ElectricLime else BorderColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onCategoriaChange(categoria) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChipsCategoriasPreview() {
    ChipsCategorias(
        categoriaSeleccionada = "Todo",
        onCategoriaChange = {},
        modifier = Modifier.padding(16.dp)
    )
}

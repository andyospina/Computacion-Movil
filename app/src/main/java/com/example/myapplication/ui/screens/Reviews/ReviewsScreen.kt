package com.example.myapplication.ui.screens.Reviews

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.components.ChipFiltro
import com.example.myapplication.ui.components.Estrellas
import com.example.myapplication.ui.components.TarjetaResena
import com.example.myapplication.ui.theme.MyApplicationTheme

private enum class FiltroReviews(val etiqueta: String) {
    RECIENTES("Recientes"),
    MEJOR_VALORADAS("Mejor valoradas"),
    CON_FOTOS("Con fotos")
}

/**
 * Pantalla "Reseñas (n)" del producto: filtros + lista completa en un
 * LazyColumn. El filtro seleccionado es estado propio de esta pantalla.
 */
@Composable
fun ReviewsScreen(
    modifier: Modifier = Modifier,
    producto: Producto,
    resenas: List<Resena>
) {
    var filtro by remember { mutableStateOf(FiltroReviews.RECIENTES) }

    val resenasFiltradas = when (filtro) {
        FiltroReviews.RECIENTES -> resenas
        FiltroReviews.MEJOR_VALORADAS -> resenas.sortedByDescending { it.puntuacion }
        FiltroReviews.CON_FOTOS -> emptyList()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reseñas (${producto.totalResenas})",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Estrellas(puntuacion = Math.round(producto.puntuacionPromedio).toInt().coerceIn(0, 5))
                    Text(
                        text = " ${producto.puntuacionPromedio}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FiltroReviews.entries.forEach { opcion ->
                    ChipFiltro(
                        texto = opcion.etiqueta,
                        seleccionado = filtro == opcion,
                        onClick = { filtro = opcion }
                    )
                }
            }
        }

        if (resenasFiltradas.isEmpty()) {
            item {
                Text(
                    text = "No hay reseñas con fotos todavía.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items = resenasFiltradas, key = { it.id }) { resena ->
                TarjetaResena(resena = resena)
            }

            item {
                Text(
                    text = "Cargar más reseñas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                        .padding(11.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "ReviewsScreen - Preview")
@Composable
fun ReviewsScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        ReviewsScreen(
            producto = DatosLocales.productoDestacado,
            resenas = DatosLocales.resenas
        )
    }
}

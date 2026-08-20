package com.example.myapplication.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Tarjeta reutilizable de producto en una lista: imagen + nombre + estrellas
 * + conteo de reseñas + chevron. Usada en Home (Tendencias) y Buscar.
 */
@Composable
fun TarjetaProducto(
    producto: Producto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImagenPlaceholder(
            modifier = Modifier.size(56.dp),
            esquinas = 6.dp
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = producto.nombre,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Estrellas(puntuacion = Math.round(producto.puntuacionPromedio).toInt().coerceIn(0, 5))
                Text(
                    text = " ${producto.puntuacionPromedio} · ${producto.totalResenas} reseñas",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "›",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaProductoPreview() {
    MyApplicationTheme(darkTheme = false) {
        TarjetaProducto(producto = DatosLocales.productoDestacado)
    }
}

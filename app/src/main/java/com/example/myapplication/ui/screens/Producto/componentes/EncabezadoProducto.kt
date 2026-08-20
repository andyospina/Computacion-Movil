package com.example.myapplication.ui.screens.Producto.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.ui.components.Estrellas
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun EncabezadoProducto(
    producto: Producto,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = producto.nombre,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Row {
            Estrellas(puntuacion = Math.round(producto.puntuacionPromedio).toInt().coerceIn(0, 5))
            val recomienda = if (producto.porcentajeRecomienda > 0) {
                " · ${producto.porcentajeRecomienda}% recomiendan"
            } else {
                ""
            }
            Text(
                text = " ${producto.puntuacionPromedio} · ${producto.totalResenas} reseñas$recomienda",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncabezadoProductoPreview() {
    MyApplicationTheme(darkTheme = false) {
        EncabezadoProducto(producto = DatosLocales.productoDestacado)
    }
}

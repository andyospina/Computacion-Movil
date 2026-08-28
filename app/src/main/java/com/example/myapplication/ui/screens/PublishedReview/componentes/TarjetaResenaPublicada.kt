package com.example.myapplication.ui.screens.PublishedReview.componentes

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.components.Estrellas
import com.example.myapplication.ui.components.ImagenPlaceholder
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun TarjetaResenaPublicada(
    resena: Resena,
    producto: Producto,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImagenPlaceholder(modifier = Modifier.size(44.dp), esquinas = 6.dp)

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = producto.nombre,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Estrellas(puntuacion = resena.puntuacion)
                Text(
                    text = " Tu calificación",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaResenaPublicadaPreview() {
    MyApplicationTheme(darkTheme = false) {
        TarjetaResenaPublicada(
            resena = DatosLocales.resenas.first(),
            producto = DatosLocales.productoDestacado
        )
    }
}

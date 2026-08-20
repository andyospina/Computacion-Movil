package com.example.myapplication.ui.screens.Perfil.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.myapplication.model.EstadoResena
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.components.Estrellas
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.StripeLight

/**
 * Tarjeta de "Mis reseñas" en Perfil: nombre del producto + estrellas,
 * comentario y una etiqueta de estado (Publicada / Borrador). Los borradores
 * usan borde punteado, como en el diseño.
 */
@Composable
fun TarjetaMiResena(
    resena: Resena,
    producto: Producto,
    modifier: Modifier = Modifier
) {
    val esBorrador = resena.estado == EstadoResena.BORRADOR
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (esBorrador) {
                    Modifier
                        .background(StripeLight)
                        .border(BorderStroke(1.dp, BorderLight), shape)
                } else {
                    Modifier.border(1.dp, BorderLight, shape)
                }
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = producto.nombre, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Estrellas(puntuacion = resena.puntuacion)
        }

        Text(
            text = resena.comentario,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (esBorrador) resena.fecha else "${resena.fecha} · Útil (${resena.votosUtiles})",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (esBorrador) "Borrador" else "Publicada",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (esBorrador) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaMiResenaPreview() {
    MyApplicationTheme(darkTheme = false) {
        TarjetaMiResena(
            resena = DatosLocales.misResenas.first(),
            producto = DatosLocales.productoDestacado
        )
    }
}

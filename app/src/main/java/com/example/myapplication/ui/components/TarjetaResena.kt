package com.example.myapplication.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextComentario

/**
 * Tarjeta reutilizable para mostrar una Resena publicada. Usada en Reviews
 * y Producto (reseñas destacadas), dentro de listas Lazy.
 */
@Composable
fun TarjetaResena(
    resena: Resena,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = resena.autor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (resena.compradorVerificado) {
                    Text(
                        text = "  ✓ Comprador verificado",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Estrellas(puntuacion = resena.puntuacion)
        }

        Text(
            text = resena.titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = resena.comentario,
            fontSize = 12.sp,
            color = TextComentario,
            lineHeight = 18.sp
        )

        Row {
            Text(
                text = "${resena.fecha} · Útil (${resena.votosUtiles})",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (resena.recomendado) {
                Text(
                    text = " · ✓ Recomendado",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
internal fun Estrellas(puntuacion: Int, fontSize: androidx.compose.ui.unit.TextUnit = 13.sp) {
    Row {
        Text(
            text = "★".repeat(puntuacion),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "★".repeat(5 - puntuacion),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaResenaPreview() {
    MyApplicationTheme(darkTheme = false) {
        TarjetaResena(
            resena = Resena(
                id = 1,
                productoId = 1,
                autor = "Emily",
                compradorVerificado = true,
                puntuacion = 5,
                titulo = "100000/10 recommend!!",
                comentario = "Excelente producto, superó mis expectativas.",
                fecha = "Jul 13, 2026",
                votosUtiles = 2,
                recomendado = true
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

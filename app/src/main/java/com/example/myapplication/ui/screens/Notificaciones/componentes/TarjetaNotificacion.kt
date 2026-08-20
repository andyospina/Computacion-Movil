package com.example.myapplication.ui.screens.Notificaciones.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Notificacion
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun TarjetaNotificacion(
    notificacion: Notificacion,
    modifier: Modifier = Modifier
) {
    val shape = if (notificacion.leida) {
        RoundedCornerShape(8.dp)
    } else {
        RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (notificacion.leida) {
                    Modifier.border(1.dp, BorderLight, shape)
                } else {
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            shape
                        )
                }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(if (notificacion.leida) BorderLight else Ink, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = notificacion.icono,
                fontSize = 14.sp,
                color = if (notificacion.leida) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
            )
        }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = notificacion.mensaje,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Text(
                text = notificacion.tiempo,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaNotificacionPreview() {
    MyApplicationTheme(darkTheme = false) {
        TarjetaNotificacion(
            notificacion = Notificacion(
                id = 1,
                icono = "★",
                mensaje = buildAnnotatedString { append("14 personas marcaron tu reseña como útil") },
                tiempo = "Hace 2 horas",
                leida = false
            )
        )
    }
}

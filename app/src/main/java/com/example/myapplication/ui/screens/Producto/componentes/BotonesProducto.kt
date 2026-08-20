package com.example.myapplication.ui.screens.Producto.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun BotonesProducto(
    modifier: Modifier = Modifier,
    onEscribirResena: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onEscribirResena,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Escribir reseña", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { /* guardado sin funcionalidad de backend */ },
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface)
        ) {
            Text(text = "Guardar", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BotonesProductoPreview() {
    MyApplicationTheme(darkTheme = false) {
        BotonesProducto(onEscribirResena = {})
    }
}

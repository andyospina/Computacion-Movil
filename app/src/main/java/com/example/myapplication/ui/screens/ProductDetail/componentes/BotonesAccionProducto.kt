package com.example.myapplication.ui.screens.ProductDetail.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun BotonesAccionProducto(
    modifier: Modifier = Modifier,
    onEscribirResenaClick: () -> Unit,
    onGuardarClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onEscribirResenaClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = Ink),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(text = "Escribir reseña", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onGuardarClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(text = "Guardar", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BotonesAccionProductoPreview() {
    BotonesAccionProducto(
        onEscribirResenaClick = {},
        onGuardarClick = {}
    )
}

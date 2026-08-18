package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BotonesNuevaResena(
    modifier: Modifier = Modifier,
    onPublicar: () -> Unit,
    onCancelar: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Cancelar",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onCancelar()
            }
        )

        Button(
            onClick = onPublicar,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD5FF00)
            ),
            shape = RoundedCornerShape(6.dp)
        ) {

            Text(
                text = "Post",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "BotonesNuevaResena - Preview")
@Composable
fun BotonesNuevaResenaPreview() {
    BotonesNuevaResena(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onPublicar = {},
        onCancelar = {}
    )
}
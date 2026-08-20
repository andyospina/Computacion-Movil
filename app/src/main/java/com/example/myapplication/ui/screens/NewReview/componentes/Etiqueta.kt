package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Etiqueta(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)

    Text(
        text = texto,
        modifier = modifier
            .clip(shape)
            .then(
                if (seleccionada) {
                    Modifier.background(MaterialTheme.colorScheme.primary)
                } else {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape)
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        fontSize = 12.sp,
        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
        color = if (seleccionada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    )
}

@Composable
@Preview(showBackground = true)
fun EtiquetaPreview() {
    MyApplicationTheme(darkTheme = false) {
        Etiqueta(texto = "Batería", seleccionada = true, onClick = {})
    }
}

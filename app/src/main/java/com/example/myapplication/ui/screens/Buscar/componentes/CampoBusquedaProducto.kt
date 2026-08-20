package com.example.myapplication.ui.screens.Buscar.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CampoBusquedaProducto(
    texto: String,
    onTextoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🔍", fontSize = 14.sp)

        BasicTextField(
            value = texto,
            onValueChange = onTextoChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                if (texto.isEmpty()) {
                    Text(
                        text = "Busca un producto…",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        )

        if (texto.isNotEmpty()) {
            Text(
                text = "✕",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onTextoChange("") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampoBusquedaProductoPreview() {
    MyApplicationTheme(darkTheme = false) {
        CampoBusquedaProducto(texto = "audifonos", onTextoChange = {})
    }
}

package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BorderLight
import com.example.myapplication.ui.theme.Gray
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.StripeLight

/**
 * Placeholder de imagen de producto: el diseño no usa fotos reales, sino un
 * patrón de rayas diagonales (repeating-linear-gradient 45°) sobre el que,
 * opcionalmente, se centra una etiqueta de texto (p. ej. "Foto del producto").
 */
@Composable
fun ImagenPlaceholder(
    modifier: Modifier = Modifier,
    esquinas: Dp = 6.dp,
    texto: String? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(esquinas))
            .background(BorderLight),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val spacing = 12.dp.toPx()
            val strokeWidth = 6.dp.toPx()
            val diagonal = size.width + size.height
            var offset = -size.height
            while (offset < diagonal) {
                drawLine(
                    color = StripeLight,
                    start = Offset(offset, 0f),
                    end = Offset(offset + size.height, size.height),
                    strokeWidth = strokeWidth
                )
                offset += spacing
            }
        }

        if (texto != null) {
            Text(
                text = texto,
                fontSize = 12.sp,
                color = Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagenPlaceholderPreview() {
    MyApplicationTheme(darkTheme = false) {
        Box(modifier = Modifier.background(Color.White)) {
            ImagenPlaceholder(
                modifier = Modifier.size(width = 300.dp, height = 170.dp),
                texto = "Foto del producto"
            )
        }
    }
}

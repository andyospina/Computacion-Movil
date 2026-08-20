package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.ImagenPlaceholder
import com.example.myapplication.ui.theme.Border
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Miniaturas de fotos adjuntas a la reseña. Sin funcionalidad real de
 * cámara/galería (fuera del alcance del sprint), solo visual.
 */
@Composable
fun FotosAdjuntas(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(2) {
            ImagenPlaceholder(
                modifier = Modifier
                    .size(58.dp)
                    .border(1.dp, Border, RoundedCornerShape(4.dp)),
                esquinas = 4.dp
            )
        }

        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FotosAdjuntasPreview() {
    MyApplicationTheme(darkTheme = false) {
        FotosAdjuntas()
    }
}

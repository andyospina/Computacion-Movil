package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

private val OPCIONES_ETIQUETAS = listOf("Calidad de audio", "Batería", "Ergonomía", "Conectividad")

@Composable
fun CampoEtiquetas(
    etiquetasSeleccionadas: Set<String>,
    onEtiquetaToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Keywords:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OPCIONES_ETIQUETAS.forEach { etiqueta ->
                Etiqueta(
                    texto = etiqueta,
                    seleccionada = etiqueta in etiquetasSeleccionadas,
                    onClick = { onEtiquetaToggle(etiqueta) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "CampoEtiquetas - Preview")
@Composable
fun CampoEtiquetasPreview() {
    MyApplicationTheme(darkTheme = false) {
        CampoEtiquetas(
            etiquetasSeleccionadas = setOf("Batería"),
            onEtiquetaToggle = {}
        )
    }
}

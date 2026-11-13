package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GraySecondary

/**
 * Opción (desactivada por defecto) para publicar la reseña con la ubicación aproximada del autor.
 * Es opt-in: la ubicación es un dato personal y solo se obtiene si el usuario lo activa y concede el permiso.
 */
@Composable
fun CampoUbicacion(
    incluirUbicacion: Boolean,
    permisoDenegado: Boolean,
    onIncluirUbicacionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.new_review_include_location), fontWeight = FontWeight.SemiBold)
                Text(
                    text = stringResource(R.string.new_review_include_location_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = GraySecondary
                )
            }
            Switch(
                checked = incluirUbicacion,
                onCheckedChange = onIncluirUbicacionChange,
                enabled = enabled
            )
        }
        if (permisoDenegado) {
            Text(
                text = stringResource(R.string.new_review_location_denied),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "CampoUbicacion - Preview")
@Composable
fun CampoUbicacionPreview() {
    CampoUbicacion(incluirUbicacion = true, permisoDenegado = false, onIncluirUbicacionChange = {})
}

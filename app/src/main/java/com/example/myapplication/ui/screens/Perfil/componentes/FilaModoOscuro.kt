package com.example.myapplication.ui.screens.Perfil.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Switch de modo oscuro. El estado real vive en ReviewLabApp (afecta a toda
 * la app); aquí solo se muestra y se notifica el cambio.
 */
@Composable
fun FilaModoOscuro(
    activo: Boolean,
    onActivoChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Modo oscuro", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Cambia la paleta de toda la app",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = activo,
            onCheckedChange = onActivoChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilaModoOscuroPreview() {
    MyApplicationTheme(darkTheme = false) {
        FilaModoOscuro(activo = false, onActivoChange = {})
    }
}

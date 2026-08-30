package com.example.myapplication.ui.screens.Profile.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun SelectorTema(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (modoOscuro) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            contentDescription = null
        )

        Text(
            text = "Modo oscuro",
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )

        Switch(
            checked = modoOscuro,
            onCheckedChange = onModoOscuroChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = ElectricLime,
                checkedThumbColor = Ink
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectorTemaPreview() {
    SelectorTema(
        modoOscuro = false,
        onModoOscuroChange = {},
        modifier = Modifier.padding(16.dp)
    )
}

package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Ink,
    secondary = DeepLime,
    onSecondary = Color.White,
    background = Ink,
    onBackground = Color.White,
    surface = Color(0xFF1B1B1D),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF232326),
    outline = GraySecondary
)

// En claro, `primary` es DeepLime: el lima eléctrico sobre el fondo claro casi no se ve y es el color por
// defecto del texto de los OutlinedButton/TextButton, del indicador de carga y del borde enfocado de los
// campos. Los botones rellenos fijan ElectricLime + Ink explícitamente, así que no cambian.
private val LightColorScheme = lightColorScheme(
    primary = DeepLime,
    onPrimary = Color.White,
    secondary = DeepLime,
    onSecondary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Paper,
    onSurfaceVariant = GraySecondary,
    outline = BorderColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

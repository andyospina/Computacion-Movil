package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tonos adicionales solo para el esquema oscuro (no forman parte de la
// paleta "1b" del diseño, que es exclusivamente clara; se derivan de ella
// para que el modo oscuro se sienta consistente con la marca).
private val SurfaceVariantDark = Color(0xFF1C1C1F)
private val OutlineDark = Color(0xFF3A3A3E)
private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)

private val LightColorScheme = lightColorScheme(
    primary = ElectricLime,
    onPrimary = Ink,
    primaryContainer = ElectricLime,
    onPrimaryContainer = Ink,
    secondary = DeepLime,
    onSecondary = Paper,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Paper,
    onSurfaceVariant = Gray,
    outline = Border,
    error = RequeridoRojo,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Ink,
    primaryContainer = ElectricLime,
    onPrimaryContainer = Ink,
    secondary = ElectricLime,
    onSecondary = Ink,
    background = Ink,
    onBackground = Paper,
    surface = Ink,
    onSurface = Paper,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Gray,
    outline = OutlineDark,
    error = ErrorDark,
    onError = OnErrorDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

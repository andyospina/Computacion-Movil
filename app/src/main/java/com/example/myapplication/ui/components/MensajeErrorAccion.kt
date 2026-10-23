package com.example.myapplication.ui.components

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Muestra un aviso breve cuando falla una acción puntual (like, seguir) sin ocultar la pantalla, y
 * avisa al ViewModel para que limpie el error y no se repita al recomponer.
 */
@Composable
fun MensajeErrorAccion(
    @StringRes messageRes: Int?,
    onShown: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(messageRes) {
        if (messageRes != null) {
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
            onShown()
        }
    }
}

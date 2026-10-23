package com.example.myapplication.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Pide POST_NOTIFICATIONS (Android 13+) una sola vez al entrar a la app con sesión iniciada.
 * Si ya se concedió no vuelve a preguntar; en versiones anteriores no hace falta pedirlo.
 */
@Composable
fun SolicitarPermisoNotificaciones() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        Log.d("Notifications", if (concedido) "Permiso aceptado" else "Permiso denegado")
    }

    LaunchedEffect(Unit) {
        val permiso = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(permiso)
        }
    }
}

package com.example.myapplication.model

import androidx.compose.ui.text.AnnotatedString

/**
 * Entidad de UI: representa una notificación en la pantalla de Notificaciones.
 */
data class Notificacion(
    val id: Int,
    val icono: String,
    val mensaje: AnnotatedString,
    val tiempo: String,
    val leida: Boolean
)

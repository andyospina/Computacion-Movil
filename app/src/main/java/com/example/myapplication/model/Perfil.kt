package com.example.myapplication.model

/**
 * Entidad de UI: representa los datos de perfil y estadísticas del usuario.
 */
data class Perfil(
    val nombre: String,
    val miembroDesde: Int,
    val totalResenas: Int,
    val votosUtiles: Int,
    val promedio: Double
) {
    val iniciales: String
        get() = nombre
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}

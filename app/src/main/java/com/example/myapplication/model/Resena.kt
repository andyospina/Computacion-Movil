package com.example.myapplication.model

enum class EstadoResena {
    PUBLICADA,
    BORRADOR
}

/**
 * Entidad de UI: representa una reseña tal como se muestra en pantalla
 * (Reviews, Producto, Perfil, Confirmación).
 */
data class Resena(
    val id: Int,
    val productoId: Int,
    val autor: String,
    val compradorVerificado: Boolean,
    val puntuacion: Int,
    val titulo: String,
    val comentario: String,
    val fecha: String,
    val votosUtiles: Int,
    val recomendado: Boolean = false,
    val estado: EstadoResena = EstadoResena.PUBLICADA
)

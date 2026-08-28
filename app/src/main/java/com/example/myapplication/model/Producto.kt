package com.example.myapplication.model

/**
 * Entidad de UI: representa el producto que se está reseñando. No incluye
 * foto real — el diseño muestra un placeholder para todas las imágenes.
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val puntuacionPromedio: Double,
    val totalResenas: Int,
    val porcentajeRecomienda: Int
)

package com.example.myapplication.ui.screens.Map

import com.example.myapplication.data.Review
import kotlin.math.floor
import kotlin.math.pow

/** Grupo de reseñas cercanas que se dibuja como un solo marcador con el número de reseñas. */
data class ReviewCluster(
    val reviews: List<Review>,
    val latitude: Double,
    val longitude: Double
) {
    val isSingle: Boolean get() = reviews.size == 1
}

private const val CELL_PIXELS = 72.0
private const val TILE_PIXELS = 256.0
private const val MAX_CLUSTER_ZOOM = 17.0

/**
 * Agrupa reseñas por cuadrícula según el zoom: cada celda mide ~[CELL_PIXELS] px en pantalla, así los
 * marcadores que quedarían encimados se funden en uno con contador y se separan al acercar el mapa.
 *
 * - La reseña [selectedId] nunca se agrupa: su tarjeta debe seguir apuntando a su propio marcador.
 * - Desde [MAX_CLUSTER_ZOOM] (calle) no se agrupa nada.
 * - Es una función pura (sin osmdroid): se prueba en JVM.
 */
fun clusterReviews(reviews: List<Review>, zoom: Double, selectedId: String? = null): List<ReviewCluster> {
    val located = reviews.filter { it.location != null }
    if (zoom >= MAX_CLUSTER_ZOOM) return located.map { it.toSingleCluster() }

    val cellDegrees = 360.0 / 2.0.pow(zoom) * CELL_PIXELS / TILE_PIXELS
    val (selected, rest) = located.partition { it.id == selectedId }

    val grouped = rest.groupBy {
        val loc = it.location!!
        floor(loc.latitude / cellDegrees).toLong() to floor(loc.longitude / cellDegrees).toLong()
    }.values.map { group ->
        ReviewCluster(
            reviews = group,
            latitude = group.map { it.location!!.latitude }.average(),
            longitude = group.map { it.location!!.longitude }.average()
        )
    }
    return grouped + selected.map { it.toSingleCluster() }
}

private fun Review.toSingleCluster() = ReviewCluster(listOf(this), location!!.latitude, location.longitude)

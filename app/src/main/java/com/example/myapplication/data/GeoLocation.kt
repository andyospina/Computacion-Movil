package com.example.myapplication.data

/** Punto geográfico del dominio, independiente de Firestore, Google Play Services u osmdroid. */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
) {
    val isValid: Boolean
        get() = latitude in -90.0..90.0 && longitude in -180.0..180.0
}

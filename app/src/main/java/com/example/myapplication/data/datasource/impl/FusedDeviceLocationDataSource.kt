package com.example.myapplication.data.datasource.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.datasource.DeviceLocationDataSource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val LOCATION_TIMEOUT_MS = 8_000L

/**
 * Implementación con Fused Location Provider. Solo necesita el permiso de ubicación aproximada:
 * para ubicar una reseña en un mapa de ciudad no hace falta la ubicación exacta (privacidad).
 */
class FusedDeviceLocationDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DeviceLocationDataSource {

    override fun hasPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    override fun isLocationEnabled(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(manager)
    }

    @SuppressLint("MissingPermission") // se comprueba en hasPermission
    override suspend fun getCurrentLocation(): GeoLocation? {
        if (!hasPermission()) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        val token = CancellationTokenSource()
        return try {
            val location = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, token.token).await()
            } ?: client.lastLocation.await()
            location?.let { GeoLocation(it.latitude, it.longitude) }
        } finally {
            token.cancel()
        }
    }
}

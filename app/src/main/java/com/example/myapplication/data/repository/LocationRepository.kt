package com.example.myapplication.data.repository

import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.datasource.DeviceLocationDataSource
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val dataSource: DeviceLocationDataSource
) {
    /**
     * Ubicación actual o null si no se pudo obtener (sin permiso, GPS apagado, tiempo agotado).
     * Es opcional por diseño: que falle no debe impedir publicar la reseña, por eso no es un Result.
     */
    suspend fun getCurrentLocation(): GeoLocation? = try {
        dataSource.getCurrentLocation()?.takeIf { it.isValid }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}

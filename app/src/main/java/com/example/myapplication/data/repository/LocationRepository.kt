package com.example.myapplication.data.repository

import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.LocationDisabledException
import com.example.myapplication.data.LocationPermissionDeniedException
import com.example.myapplication.data.LocationUnavailableException
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.DeviceLocationDataSource
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val dataSource: DeviceLocationDataSource
) {
    /** ¿Está encendido el GPS / servicio de ubicación del dispositivo? */
    fun isLocationEnabled(): Boolean = dataSource.isLocationEnabled()

    /**
     * Ubicación actual. Cada causa de fallo tiene su excepción y su mensaje (permiso, GPS apagado, sin
     * señal) para que la pantalla le diga al usuario qué hacer; compartir la ubicación es todo o nada:
     * no se publica "a medias" con la ubicación en null.
     */
    suspend fun getCurrentLocation(): Result<GeoLocation> = runCatchingApi {
        if (!dataSource.hasPermission()) throw LocationPermissionDeniedException()
        if (!dataSource.isLocationEnabled()) throw LocationDisabledException()
        dataSource.getCurrentLocation()?.takeIf { it.isValid } ?: throw LocationUnavailableException()
    }
}

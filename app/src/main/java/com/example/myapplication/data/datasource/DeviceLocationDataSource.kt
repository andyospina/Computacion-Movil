package com.example.myapplication.data.datasource

import com.example.myapplication.data.GeoLocation

/** Ubicación actual del dispositivo. */
interface DeviceLocationDataSource {
    /** ¿La app tiene permiso de ubicación (aproximada o precisa)? */
    fun hasPermission(): Boolean

    /** ¿Está activo el servicio de ubicación (GPS) del sistema? */
    fun isLocationEnabled(): Boolean

    /** Posición actual, o null si no hay permiso, el GPS está apagado o no responde a tiempo. */
    suspend fun getCurrentLocation(): GeoLocation?
}

package com.example.myapplication.data.datasource

import com.example.myapplication.data.GeoLocation

/** Ubicación actual del dispositivo. Devuelve null si no hay permiso, el GPS está apagado o no responde a tiempo. */
interface DeviceLocationDataSource {
    suspend fun getCurrentLocation(): GeoLocation?
}

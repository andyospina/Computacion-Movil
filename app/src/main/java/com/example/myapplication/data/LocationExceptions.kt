package com.example.myapplication.data

/** El usuario no concedió (o retiró) el permiso de ubicación. */
class LocationPermissionDeniedException : IllegalStateException("Permiso de ubicación no concedido")

/** La ubicación del dispositivo (GPS) está apagada en los ajustes del sistema. */
class LocationDisabledException : IllegalStateException("La ubicación del dispositivo está desactivada")

/** Hay permiso y GPS, pero no se obtuvo una posición a tiempo (sin señal, interiores, etc.). */
class LocationUnavailableException : IllegalStateException("No se pudo obtener la ubicación actual")

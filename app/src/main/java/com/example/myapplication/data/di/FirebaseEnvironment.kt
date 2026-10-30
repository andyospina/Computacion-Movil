package com.example.myapplication.data.di

import com.example.myapplication.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Punto único que decide a qué Firebase habla la app: en debug con el emulador activo se conecta a
 * Firebase Emulator Suite; en cualquier otro caso (release) a los servicios reales. Los módulos de
 * Hilt piden los clientes aquí, así ningún DataSource ni Repository decide el entorno por su cuenta.
 *
 * `10.0.2.2` es el computador visto desde el Android Emulator. En un teléfono físico hay que cambiar
 * [EMULATOR_HOST] por la IP del computador en la red local.
 */
object FirebaseEnvironment {

    const val EMULATOR_HOST = "10.0.2.2"
    const val FIRESTORE_PORT = 8080
    const val AUTH_PORT = 9099
    const val STORAGE_PORT = 9199

    val usesEmulator: Boolean get() = BuildConfig.DEBUG && BuildConfig.USE_FIREBASE_EMULATOR

    // useEmulator solo puede llamarse antes del primer uso de cada cliente: los módulos son singleton,
    // así que cada uno pasa por aquí una única vez.
    fun firestore(): FirebaseFirestore = FirebaseFirestore.getInstance().also {
        if (usesEmulator) it.useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
    }

    fun auth(): FirebaseAuth = FirebaseAuth.getInstance().also {
        if (usesEmulator) it.useEmulator(EMULATOR_HOST, AUTH_PORT)
    }

    fun storage(): FirebaseStorage = FirebaseStorage.getInstance().also {
        if (usesEmulator) it.useEmulator(EMULATOR_HOST, STORAGE_PORT)
    }
}

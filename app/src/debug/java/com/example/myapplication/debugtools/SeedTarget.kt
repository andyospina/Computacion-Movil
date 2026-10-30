package com.example.myapplication.debugtools

import android.content.Context
import com.example.myapplication.data.di.FirebaseEnvironment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Destino de una carga de datos. Cada destino tiene su propio cliente de Firestore (una FirebaseApp
 * aparte), así que cargar en el emulador nunca puede terminar en producción por compartir instancia.
 */
enum class SeedTarget(val label: String) {
    EMULATOR("Emulador local"),
    PRODUCTION("Firebase real (producción)")
}

object SeedClients {

    /** Cliente de Firestore del destino; el de producción no usa el emulador aunque debug lo use. */
    @Synchronized
    fun firestore(context: Context, target: SeedTarget): FirebaseFirestore {
        val appName = "seed-${target.name.lowercase()}"
        val existing = FirebaseApp.getApps(context).firstOrNull { it.name == appName }
        if (existing != null) return FirebaseFirestore.getInstance(existing)

        val app = FirebaseApp.initializeApp(context, FirebaseApp.getInstance().options, appName)
        return FirebaseFirestore.getInstance(app).also {
            if (target == SeedTarget.EMULATOR) {
                it.useEmulator(FirebaseEnvironment.EMULATOR_HOST, FirebaseEnvironment.FIRESTORE_PORT)
            }
        }
    }

    fun projectId(): String = FirebaseApp.getInstance().options.projectId.orEmpty()
}

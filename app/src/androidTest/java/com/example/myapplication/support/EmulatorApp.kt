package com.example.myapplication.support

import android.Manifest
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.data.di.FirebaseEnvironment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL

private const val TEST_APP_NAME = "integration-tests"
private const val LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"

/**
 * Clientes de Firebase de las pruebas: una `FirebaseApp` propia («integration-tests») conectada al
 * Emulator Suite, que nunca es la instancia por defecto de la app. Así sembrar y limpiar datos no
 * interfiere con el cliente real que usa la app bajo prueba (también apuntado al emulador en debug) y
 * no puede tocar producción aunque el emulador esté apagado (la prueba falla en vez de escribir).
 */
object EmulatorApp {

    private var firestoreConfigured = false
    private var authConfigured = false

    @Synchronized
    private fun app(): FirebaseApp {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        grantPermissions()
        return FirebaseApp.getApps(context).firstOrNull { it.name == TEST_APP_NAME }
            ?: FirebaseApp.initializeApp(context, FirebaseApp.getInstance().options, TEST_APP_NAME)
    }

    @Synchronized
    fun firestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance(app())
        if (!firestoreConfigured) {
            // Primero los ajustes: asignarlos después descartaría el host del emulador.
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build()
            firestore.useEmulator(FirebaseEnvironment.EMULATOR_HOST, FirebaseEnvironment.FIRESTORE_PORT)
            firestoreConfigured = true
        }
        return firestore
    }

    @Synchronized
    fun auth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance(app())
        if (!authConfigured) {
            auth.useEmulator(FirebaseEnvironment.EMULATOR_HOST, FirebaseEnvironment.AUTH_PORT)
            authConfigured = true
        }
        return auth
    }

    /** Crea una cuenta en el Auth del emulador y devuelve su uid; la sesión de la prueba queda cerrada. */
    suspend fun createAuthUser(email: String, password: String): String {
        val auth = auth()
        val uid = auth.createUserWithEmailAndPassword(email, password).await().user!!.uid
        auth.signOut()
        return uid
    }

    /**
     * Borra todas las cuentas del Auth del emulador (endpoint propio del Emulator Suite, solo existe
     * ahí: contra producción no hace nada porque la URL es la del emulador).
     */
    fun clearAuthAccounts() {
        val projectId = FirebaseApp.getInstance().options.projectId
        val url = URL("http://${FirebaseEnvironment.EMULATOR_HOST}:${FirebaseEnvironment.AUTH_PORT}" +
            "/emulator/v1/projects/$projectId/accounts")
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "DELETE"
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            check(connection.responseCode in 200..299) { "No se pudo limpiar Auth: HTTP ${connection.responseCode}" }
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Android 17 exige ACCESS_LOCAL_NETWORK para hablar con 10.0.2.2 (sin él Firestore queda «offline»
     * sin dar error) y Android 13+ pregunta POST_NOTIFICATIONS al entrar a Home: ambos se conceden
     * antes de la prueba para que ningún diálogo del sistema interfiera.
     */
    fun grantPermissions() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val packageName = instrumentation.targetContext.packageName
        val permissions = buildList {
            add(LOCAL_NETWORK_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.forEach { permission ->
            try {
                instrumentation.uiAutomation.grantRuntimePermission(packageName, permission)
            } catch (_: Exception) {
                // Permiso inexistente en esta versión de Android: no hace falta concederlo
            }
        }
    }
}

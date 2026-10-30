package com.example.myapplication.support

import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.data.di.FirebaseEnvironment
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.SeedLoader
import com.example.myapplication.debugtools.SeedTarget
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before

private const val TEST_APP_NAME = "integration-tests"
private const val TIMEOUT_MS = 20_000L
private const val LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"
private val COLLECTIONS = listOf("users", "products", "reviews", "follows", "saved")

/**
 * Base de las pruebas de integración: un cliente de Firestore propio (FirebaseApp aparte) conectado al
 * emulador, sin caché en disco, que se limpia antes y después de cada prueba. Nunca usa la instancia
 * por defecto, así que no puede tocar producción aunque el emulador esté apagado (la prueba falla).
 * Requiere el emulador encendido: `firebase emulators:start --config firebase.emulator.json`.
 */
abstract class FirestoreEmulatorTest {

    protected lateinit var firestore: FirebaseFirestore

    @Before
    fun connectAndClean() {
        firestore = emulatorFirestore()
        runBlocking { clearAll() }
    }

    @After
    fun cleanAndReconnect() {
        runBlocking {
            firestore.enableNetwork().await()
            clearAll()
        }
    }

    protected fun seed(dataset: SeedDataset) = runBlocking {
        val report = SeedLoader(firestore, SeedTarget.EMULATOR, projectId = "emulator").load(dataset)
        check(report.success) { "No se pudo preparar el escenario: ${report.errors}" }
    }

    /** Ejecuta la prueba con un tope de tiempo: si el emulador no responde, falla en vez de colgarse. */
    protected fun test(block: suspend CoroutineScope.() -> Unit) = runBlocking { withTimeout(TIMEOUT_MS) { block() } }

    private suspend fun clearAll() {
        COLLECTIONS.forEach { name ->
            firestore.collection(name).get().await().documents.chunked(400).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { batch.delete(it.reference) }
                batch.commit().await()
            }
        }
    }

    private companion object {
        @Synchronized
        fun emulatorFirestore(): FirebaseFirestore {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val context = instrumentation.targetContext
            // Android 17: sin este permiso el acceso a 10.0.2.2 se queda esperando sin dar error
            // (Gradle lo concede solo si es un permiso normal; aquí se garantiza para cualquier ejecución).
            instrumentation.uiAutomation.grantRuntimePermission(context.packageName, LOCAL_NETWORK_PERMISSION)
            val existing = FirebaseApp.getApps(context).firstOrNull { it.name == TEST_APP_NAME }
            if (existing != null) return FirebaseFirestore.getInstance(existing)
            val app = FirebaseApp.initializeApp(context, FirebaseApp.getInstance().options, TEST_APP_NAME)
            return FirebaseFirestore.getInstance(app).also {
                // Primero los ajustes: asignarlos después descartaría el host del emulador.
                it.firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                    .build()
                it.useEmulator(FirebaseEnvironment.EMULATOR_HOST, FirebaseEnvironment.FIRESTORE_PORT)
            }
        }
    }
}

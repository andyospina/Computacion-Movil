package com.example.myapplication.support

import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.SeedLoader
import com.example.myapplication.debugtools.SeedTarget
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before

private const val TIMEOUT_MS = 20_000L
private val COLLECTIONS = listOf("users", "products", "reviews", "follows", "saved", "comments")

/**
 * Base de las pruebas de integración: un cliente de Firestore propio (FirebaseApp aparte, ver
 * [EmulatorApp]) conectado al emulador, sin caché en disco, que se limpia antes y después de cada
 * prueba. Requiere el emulador encendido: `firebase emulators:start --config firebase.emulator.json`.
 */
abstract class FirestoreEmulatorTest {

    protected lateinit var firestore: FirebaseFirestore

    @Before
    fun connectAndClean() {
        firestore = EmulatorApp.firestore()
        runBlocking { clearAll() }
        EmulatorApp.clearAuthAccounts()
    }

    @After
    fun cleanAndReconnect() {
        runBlocking {
            firestore.enableNetwork().await()
            clearAll()
        }
        EmulatorApp.clearAuthAccounts()
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
}

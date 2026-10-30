package com.example.myapplication.debugtools

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Resultado de una carga: cuántos documentos entraron por colección y qué falló. */
data class SeedReport(
    val inserted: Map<String, Int>,
    val errors: List<String>
) {
    val totalInserted get() = inserted.values.sum()
    val success get() = errors.isEmpty()
}

/** La carga en producción exige repetir el id del proyecto: evita escrituras accidentales. */
class ProductionNotConfirmedException(expected: String) :
    IllegalStateException("Carga en producción rechazada: escribe el id del proyecto «$expected» para confirmar")

/**
 * Escribe un [SeedDataset] en el Firestore recibido. El destino lo decide quien lo construye con
 * [SeedClients]; el loader no sabe de entornos, salvo la guarda de producción.
 */
class SeedLoader(
    private val firestore: FirebaseFirestore,
    private val target: SeedTarget,
    private val projectId: String,
    private val confirmedProjectId: String? = null
) {

    suspend fun load(dataset: SeedDataset): SeedReport {
        requireConfirmed(target, projectId, confirmedProjectId)
        val inserted = linkedMapOf<String, Int>()
        val errors = mutableListOf<String>()

        suspend fun write(collection: String, items: List<Pair<String, Any>>) {
            try {
                // Los lotes de Firestore admiten 500 operaciones.
                items.chunked(BATCH_SIZE).forEach { chunk ->
                    val batch = firestore.batch()
                    chunk.forEach { (id, doc) -> batch.set(firestore.collection(collection).document(id), doc) }
                    batch.commit().await()
                }
                inserted[collection] = items.size
            } catch (e: Exception) {
                inserted[collection] = 0
                errors += "$collection: ${e.message}"
            }
        }

        write("users", dataset.users.map { it.id to it })
        write("products", dataset.products.map { it.id to it })
        write("reviews", dataset.reviews.map { it.id to it })
        write("follows", dataset.follows.map { "${it.followerId}_${it.followedId}" to it })
        write("saved", dataset.saved.map { "${it.userId}_${it.productId}" to it })
        return SeedReport(inserted, errors)
    }

    companion object {
        private const val BATCH_SIZE = 400

        /** Cortar aquí, antes de construir ningún lote, garantiza que una carga rechazada no escribe nada. */
        fun requireConfirmed(target: SeedTarget, projectId: String, confirmedProjectId: String?) {
            if (target == SeedTarget.PRODUCTION && confirmedProjectId != projectId) {
                throw ProductionNotConfirmedException(projectId)
            }
        }
    }
}

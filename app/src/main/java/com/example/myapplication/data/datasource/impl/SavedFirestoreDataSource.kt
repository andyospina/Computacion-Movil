package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.SavedRemoteDataSource
import com.example.myapplication.data.dto.FirestoreSavedDto
import com.example.myapplication.data.dto.savedDocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

internal const val SAVED_COLLECTION = "saved"

class SavedFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : SavedRemoteDataSource {

    private val saved get() = firestore.collection(SAVED_COLLECTION)

    // Se ordena en el cliente: where + orderBy exigiría un índice compuesto. Un guardado recién hecho
    // aún no tiene createdAt resuelto y va primero.
    override fun observeSavedProductIds(userId: String): Flow<List<String>> =
        saved.whereEqualTo("userId", userId).observe().map { snapshot ->
            snapshot.toObjects(FirestoreSavedDto::class.java)
                .sortedByDescending { it.createdAt ?: Date(Long.MAX_VALUE) }
                .map { it.productId }
        }

    override suspend fun save(userId: String, productId: String) {
        saved.document(savedDocumentId(userId, productId))
            .set(FirestoreSavedDto(userId = userId, productId = productId))
            .await()
    }

    override suspend fun unsave(userId: String, productId: String) {
        saved.document(savedDocumentId(userId, productId)).delete().await()
    }
}

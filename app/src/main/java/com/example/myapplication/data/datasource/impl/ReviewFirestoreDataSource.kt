package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.dto.toFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

internal const val REVIEWS_COLLECTION = "reviews"

class ReviewFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRemoteDataSource {

    private val reviews get() = firestore.collection(REVIEWS_COLLECTION)

    override suspend fun getReviewsByProduct(productId: String): List<ReviewData> =
        query(reviews.whereEqualTo("productId", productId))

    override suspend fun getReviewsByUser(userId: String): List<ReviewData> =
        query(reviews.whereEqualTo("userId", userId))

    override suspend fun getReviewById(id: String): ReviewData {
        val snapshot = reviews.document(id).get().await()
        if (!snapshot.exists()) throw NotFoundException("reviews/$id no existe")
        return snapshot.toObject(FirestoreReviewDto::class.java)
            ?: throw NotFoundException("reviews/$id no se pudo convertir")
    }

    // Firestore genera el id del documento; vuelve a la app al volver a consultar la reseña.
    override suspend fun createReview(review: NewReviewDto) {
        reviews.add(review.toFirestoreDto()).await()
    }

    // update() cambia solo estos campos: autor, producto y fecha se conservan.
    override suspend fun updateReview(id: String, review: UpdateReviewDto) {
        reviews.document(id).update(
            mapOf(
                "rating" to review.rating,
                "title" to review.title,
                "body" to review.body,
                "recommended" to review.recommended
            )
        ).await()
    }

    override suspend fun deleteReview(id: String) {
        reviews.document(id).delete().await()
    }

    // Se ordena en el cliente: combinar where + orderBy exigiría un índice compuesto en Firestore.
    // Una reseña recién creada todavía no tiene createdAt resuelto y va primero.
    private suspend fun query(query: Query): List<ReviewData> =
        query.get().await().toObjects(FirestoreReviewDto::class.java)
            .sortedByDescending { it.createdAt ?: Date(Long.MAX_VALUE) }
}

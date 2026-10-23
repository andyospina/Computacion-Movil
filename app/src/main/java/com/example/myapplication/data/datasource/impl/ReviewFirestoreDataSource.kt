package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.data.dto.toFirestoreDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    override fun observeReviewsByProduct(productId: String): Flow<List<ReviewData>> =
        reviews.whereEqualTo("productId", productId).observe()
            .map { it.toObjects(FirestoreReviewDto::class.java).newestFirst() }

    // whereIn admite hasta 30 valores: con más usuarios se escucha una consulta por grupo y se unen.
    override fun observeReviewsByUsers(userIds: List<String>): Flow<List<ReviewData>> {
        val ids = userIds.distinct()
        if (ids.isEmpty()) return flowOf(emptyList())
        val flows = ids.chunked(WHERE_IN_LIMIT).map { chunk ->
            reviews.whereIn("userId", chunk).observe().map { it.toObjects(FirestoreReviewDto::class.java) }
        }
        return combine(flows) { parts -> parts.flatMap { it }.newestFirst() }
    }

    // arrayUnion / arrayRemove son atómicos: dos likes simultáneos no se pisan.
    override suspend fun likeReview(reviewId: String, userId: String) {
        reviews.document(reviewId).update("likedBy", FieldValue.arrayUnion(userId)).await()
    }

    override suspend fun unlikeReview(reviewId: String, userId: String) {
        reviews.document(reviewId).update("likedBy", FieldValue.arrayRemove(userId)).await()
    }

    private suspend fun query(query: Query): List<ReviewData> =
        query.get().await().toObjects(FirestoreReviewDto::class.java).newestFirst()

    // Se ordena en el cliente: combinar where + orderBy exigiría un índice compuesto en Firestore.
    // Una reseña recién creada todavía no tiene createdAt resuelto y va primero.
    private fun List<FirestoreReviewDto>.newestFirst() =
        sortedByDescending { it.createdAt ?: Date(Long.MAX_VALUE) }
}

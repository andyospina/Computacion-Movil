package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Las listas se devuelven de la más reciente a la más antigua. */
interface ReviewRemoteDataSource {
    suspend fun getReviewsByProduct(productId: String): List<ReviewData>
    suspend fun getReviewsByUser(userId: String): List<ReviewData>
    suspend fun getReviewById(id: String): ReviewData
    suspend fun createReview(review: NewReviewDto)
    suspend fun updateReview(id: String, review: UpdateReviewDto)
    suspend fun deleteReview(id: String)

    /** Emite la lista de nuevo cada vez que cambia una reseña del producto (likes incluidos). */
    fun observeReviewsByProduct(productId: String): Flow<List<ReviewData>>

    /** Reseñas escritas por cualquiera de [userIds] (feed de usuarios seguidos, perfil de otro usuario). */
    fun observeReviewsByUsers(userIds: List<String>): Flow<List<ReviewData>>

    /** Reseñas a las que [userId] les dio like (pantalla «Me gustaron»), en tiempo real. */
    fun observeReviewsLikedBy(userId: String): Flow<List<ReviewData>> =
        flow { throw UnsupportedOperationException("Este backend no tiene likes") }

    suspend fun likeReview(reviewId: String, userId: String)
    suspend fun unlikeReview(reviewId: String, userId: String)
}

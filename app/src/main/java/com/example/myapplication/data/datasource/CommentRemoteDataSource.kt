package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.CommentData
import com.example.myapplication.data.dto.NewCommentDto
import kotlinx.coroutines.flow.Flow

/** Comentarios de las reseñas. Solo Firestore: el backend Express no tiene comentarios. */
interface CommentRemoteDataSource {
    /** Comentarios de [reviewId] del más antiguo al más reciente (orden de conversación), en tiempo real. */
    fun observeComments(reviewId: String): Flow<List<CommentData>>

    suspend fun addComment(comment: NewCommentDto)
    suspend fun deleteComment(commentId: String)
}

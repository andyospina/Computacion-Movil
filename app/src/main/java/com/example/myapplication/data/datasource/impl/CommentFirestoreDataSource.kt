package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.CommentRemoteDataSource
import com.example.myapplication.data.dto.CommentData
import com.example.myapplication.data.dto.FirestoreCommentDto
import com.example.myapplication.data.dto.NewCommentDto
import com.example.myapplication.data.dto.toFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

internal const val COMMENTS_COLLECTION = "comments"

class CommentFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRemoteDataSource {

    private val comments get() = firestore.collection(COMMENTS_COLLECTION)

    // Se ordena en el cliente: where + orderBy exigiría un índice compuesto. Un comentario recién enviado
    // aún no tiene createdAt resuelto y va al final, que es donde el usuario lo espera.
    override fun observeComments(reviewId: String): Flow<List<CommentData>> =
        comments.whereEqualTo("reviewId", reviewId).observe().map { snapshot ->
            snapshot.toObjects(FirestoreCommentDto::class.java)
                .sortedBy { it.createdAt ?: Date(Long.MAX_VALUE) }
        }

    override suspend fun addComment(comment: NewCommentDto) {
        comments.add(comment.toFirestoreDto()).await()
    }

    override suspend fun deleteComment(commentId: String) {
        comments.document(commentId).delete().await()
    }
}

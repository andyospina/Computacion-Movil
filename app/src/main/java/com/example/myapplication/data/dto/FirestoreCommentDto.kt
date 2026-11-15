package com.example.myapplication.data.dto

import com.example.myapplication.data.Comment
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/** Contrato de un comentario que llega de cualquier backend (como [ReviewData] para las reseñas). */
interface CommentData {
    /** [reviewAuthorId]: autor de la reseña comentada, que también puede borrar los comentarios. */
    fun toComment(currentUserId: String?, reviewAuthorId: String?): Comment
}

/**
 * Documento `comments/{id}`: un comentario sobre la reseña [reviewId]. Colección de primer nivel (como
 * `follows` y `saved`) para consultarla con un solo `whereEqualTo` y limpiarla sin recorrer subcolecciones.
 * Copia el autor ([FirestoreAuthorDto]) para pintar la conversación sin consultar `users`; la Cloud Function
 * `onCommentCreated` rehace esa copia con los datos reales.
 */
data class FirestoreCommentDto(
    @DocumentId val id: String = "",
    val reviewId: String = "",
    val userId: String = "",
    val text: String = "",
    val user: FirestoreAuthorDto = FirestoreAuthorDto(),
    @ServerTimestamp val createdAt: Date? = null
) : CommentData {

    override fun toComment(currentUserId: String?, reviewAuthorId: String?): Comment {
        val isMine = currentUserId != null && currentUserId == userId
        return Comment(
            id = id,
            reviewId = reviewId,
            userId = userId,
            authorName = user.name.ifBlank { user.username },
            authorImage = user.profileImage,
            text = text,
            createdAtMillis = createdAt?.time,
            isMine = isMine,
            canDelete = isMine || (currentUserId != null && currentUserId == reviewAuthorId)
        )
    }
}

/** Datos para publicar un comentario: el Repository arma la copia del autor. */
data class NewCommentDto(
    val reviewId: String,
    val userId: String,
    val text: String,
    val authorName: String,
    val authorUsername: String,
    val authorImage: String?
)

fun NewCommentDto.toFirestoreDto() = FirestoreCommentDto(
    reviewId = reviewId,
    userId = userId,
    text = text,
    user = FirestoreAuthorDto(name = authorName, username = authorUsername, profileImage = authorImage)
)

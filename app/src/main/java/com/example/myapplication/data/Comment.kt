package com.example.myapplication.data

/** Largo máximo de un comentario (debe coincidir con la regla de `comments` en firestore.rules). */
const val MAX_COMMENT_LENGTH = 500

/**
 * Comentario dentro de la conversación de una reseña.
 * [canDelete] es verdadero para su autor y para el autor de la reseña (que modera su propia conversación).
 */
data class Comment(
    val id: String,
    val reviewId: String,
    val userId: String,
    val authorName: String,
    val authorImage: String?,
    val text: String,
    // Momento de publicación; null mientras el servidor aún no resuelve la fecha de un comentario recién enviado
    val createdAtMillis: Long?,
    val isMine: Boolean,
    val canDelete: Boolean
)

package com.example.myapplication.data.dto

import com.example.myapplication.data.Review
import com.example.myapplication.data.ReviewStatus
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Copia desnormalizada del autor dentro de la reseña, para mostrarla sin consultar `users`.
 * Solo se copian los campos que la tarjeta de reseña usa. Si el autor cambia su perfil, esta copia
 * queda desactualizada: no hay sincronización automática.
 */
data class FirestoreAuthorDto(
    val name: String = "",
    val username: String = "",
    val profileImage: String? = null
)

/**
 * Documento `reviews/{id}`. Valores por defecto: requisito de Firestore para deserializar.
 * `likedBy` guarda los uid de quienes dieron like: el contador es su tamaño y las reglas solo dejan
 * que cada usuario agregue o quite su propio uid.
 */
data class FirestoreReviewDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val rating: Int = 0,
    val title: String = "",
    val body: String = "",
    val recommended: Boolean = true,
    val user: FirestoreAuthorDto = FirestoreAuthorDto(),
    val likedBy: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Date? = null
) : ReviewData {

    override fun toReview(currentUserId: String?): Review = Review(
        id = id,
        productId = productId,
        author = user.name.ifBlank { user.username },
        verifiedBuyer = false,
        rating = rating,
        title = title,
        body = body,
        date = createdAt.toDisplayDate(),
        usefulCount = 0,
        recommended = recommended,
        hasPhoto = false,
        status = ReviewStatus.PUBLISHED,
        userId = userId,
        productName = productName,
        likesCount = likedBy.size,
        likedByMe = currentUserId != null && currentUserId in likedBy
    )
}

fun NewReviewDto.toFirestoreDto() = FirestoreReviewDto(
    userId = userId,
    productId = productId,
    productName = productName,
    rating = rating,
    title = title,
    body = body,
    recommended = recommended,
    user = FirestoreAuthorDto(name = authorName, username = authorUsername, profileImage = authorImage)
)

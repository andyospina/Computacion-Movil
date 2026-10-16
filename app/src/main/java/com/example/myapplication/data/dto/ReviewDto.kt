package com.example.myapplication.data.dto

import com.example.myapplication.data.Review
import com.example.myapplication.data.ReviewStatus
import kotlinx.serialization.Serializable

/** Respuesta de GET/POST/PUT /reviews. `user` y `product` solo vienen en /reviews, no en /products. */
@Serializable
data class ReviewDto(
    val id: Int,
    val rating: Int,
    val title: String,
    val body: String,
    val recommended: Boolean = true,
    val userId: Int,
    val productId: Int,
    val createdAt: String? = null,
    val user: ReviewAuthorDto? = null,
    val product: ReviewProductDto? = null
) : ReviewData {

    override fun toReview(): Review = Review(
        id = id.toString(),
        productId = productId.toString(),
        author = user?.let { it.name.ifBlank { it.username } }.orEmpty(),
        verifiedBuyer = false,
        rating = rating,
        title = title,
        body = body,
        date = createdAt.toDisplayDate(),
        usefulCount = 0,
        recommended = recommended,
        hasPhoto = false,
        status = ReviewStatus.PUBLISHED,
        userId = userId.toString(),
        productName = product?.name.orEmpty()
    )
}

@Serializable
data class ReviewAuthorDto(
    val id: Int,
    val username: String = "",
    val name: String = ""
)

@Serializable
data class ReviewProductDto(
    val id: Int,
    val name: String = ""
)

/** Cuerpo de POST /reviews. */
@Serializable
data class CreateReviewDto(
    val rating: Int,
    val title: String,
    val body: String,
    val recommended: Boolean,
    val userId: Int,
    val productId: Int
)

/** Cuerpo de PUT /reviews/{id}: el autor y el producto no cambian. */
@Serializable
data class UpdateReviewDto(
    val rating: Int,
    val title: String,
    val body: String,
    val recommended: Boolean
)

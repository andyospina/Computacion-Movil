package com.example.myapplication.data.dto

import com.example.myapplication.data.Product
import kotlinx.serialization.Serializable

private const val MAX_STARS = 5

/** Respuesta de GET /products y GET /products/{id} (incluye sus reseñas, sin autor). */
@Serializable
data class ProductDto(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double = 0.0,
    val reviews: List<ReviewDto> = emptyList()
) : ProductData {

    override fun toProduct(): Product =
        productOf(id.toString(), name, category, rating, reviews.map { it.rating to it.recommended })
}

/** Calcula las estadísticas de un producto a partir de (puntuación, recomendada) de sus reseñas. */
internal fun productOf(
    id: String,
    name: String,
    category: String,
    rating: Double,
    reviews: List<Pair<Int, Boolean>>
): Product {
    val total = reviews.size
    return Product(
        id = id,
        name = name,
        category = category,
        rating = rating,
        reviewCount = total,
        recommendPercent = if (total == 0) 0 else reviews.count { it.second } * 100 / total,
        ratingBreakdown = (MAX_STARS downTo 1).map { stars ->
            stars to if (total == 0) 0f else reviews.count { it.first == stars }.toFloat() / total
        }
    )
}

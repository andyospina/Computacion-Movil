package com.example.myapplication.data.dto

import com.example.myapplication.data.Product
import com.google.firebase.firestore.DocumentId

/** Documento `products/{id}`. Las estadísticas no se guardan: se calculan con sus reseñas. */
data class FirestoreProductDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = ""
)

/** Producto de Firestore junto con las reseñas ya consultadas para calcular sus estadísticas. */
class FirestoreProductData(
    private val product: FirestoreProductDto,
    private val reviews: List<FirestoreReviewDto>
) : ProductData {

    override fun toProduct(): Product {
        val average = if (reviews.isEmpty()) 0.0
        else Math.round(reviews.map { it.rating }.average() * 10) / 10.0
        return productOf(
            id = product.id,
            name = product.name,
            category = product.category,
            rating = average,
            reviews = reviews.map { it.rating to it.recommended }
        )
    }
}

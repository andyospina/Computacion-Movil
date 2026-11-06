package com.example.myapplication.e2e

import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.FirestoreUserDto
import java.util.Date

/**
 * Escenario fijo de las pruebas e2e (a diferencia del Seeder con Faker, aquí los textos son conocidos
 * para poder verificarlos): un producto con dos reseñas de dos autores. La reseña de Ana es la más
 * reciente, así que es la «primera» en la ficha del producto y en la lista de reseñas.
 */
object E2EData {

    const val PRODUCT_ID = "e2e-producto"
    const val PRODUCT_NAME = "Audífonos E2E"

    const val ANA_ID = "e2e-ana"
    const val ANA_NAME = "Ana Pérez"
    const val ANA_USERNAME = "ana_e2e"
    const val ANA_BIO = "Bio de Ana E2E"
    const val ANA_COUNTRY = "Colombia"
    const val ANA_REVIEW_TITLE = "Excelente sonido"
    const val ANA_RATING = 5

    const val LUIS_ID = "e2e-luis"
    const val LUIS_NAME = "Luis Gómez"
    const val LUIS_REVIEW_TITLE = "Aceptables"
    const val LUIS_RATING = 3

    private const val HOUR_MS = 3_600_000L

    val product = FirestoreProductDto(id = PRODUCT_ID, name = PRODUCT_NAME, category = "Electrónica")

    val ana = FirestoreUserDto(
        id = ANA_ID, username = ANA_USERNAME, name = ANA_NAME, email = "ana@e2e.test",
        country = ANA_COUNTRY, bio = ANA_BIO, createdAt = Date(System.currentTimeMillis() - 400 * 24 * HOUR_MS)
    )

    val luis = FirestoreUserDto(
        id = LUIS_ID, username = "luis_e2e", name = LUIS_NAME, email = "luis@e2e.test",
        country = "Chile", bio = "Bio de Luis", createdAt = Date(System.currentTimeMillis() - 200 * 24 * HOUR_MS)
    )

    private fun review(id: String, author: FirestoreUserDto, rating: Int, title: String, hoursAgo: Long) =
        FirestoreReviewDto(
            id = id, userId = author.id, productId = PRODUCT_ID, productName = PRODUCT_NAME,
            rating = rating, title = title, body = "Cuerpo de la reseña $title", recommended = rating >= 4,
            user = FirestoreAuthorDto(name = author.name, username = author.username),
            likedBy = emptyList(), createdAt = Date(System.currentTimeMillis() - hoursAgo * HOUR_MS)
        )

    val anaReview = review("e2e-review-ana", ana, ANA_RATING, ANA_REVIEW_TITLE, hoursAgo = 1)
    val luisReview = review("e2e-review-luis", luis, LUIS_RATING, LUIS_REVIEW_TITLE, hoursAgo = 2)

    /** Promedio de las dos reseñas: (5 + 3) / 2 */
    const val AVERAGE_RATING = "4.0"
    const val REVIEW_COUNT = 2

    /** Solo la reseña de Ana (5 estrellas) es recomendada: 1 de 2 */
    const val RECOMMEND_PERCENT = 50
}

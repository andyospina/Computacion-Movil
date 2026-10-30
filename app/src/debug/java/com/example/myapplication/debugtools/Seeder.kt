package com.example.myapplication.debugtools

import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreFollowDto
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.FirestoreSavedDto
import com.example.myapplication.data.dto.FirestoreUserDto
import com.github.javafaker.Faker
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID

/** Conjunto coherente de documentos listo para escribirse en Firestore. */
data class SeedDataset(
    val users: List<FirestoreUserDto>,
    val products: List<FirestoreProductDto>,
    val reviews: List<FirestoreReviewDto>,
    val follows: List<FirestoreFollowDto>,
    val saved: List<FirestoreSavedDto> = emptyList()
) {
    val totalDocuments get() = users.size + products.size + reviews.size + follows.size + saved.size
}

/**
 * Genera datos ficticios con la misma forma que los modelos de Firestore de la app. No toca Firebase:
 * solo produce objetos, así sirve igual para el emulador, para Firebase real y para las pruebas.
 * Con [seed] fijo la salida (salvo los UUID) es reproducible.
 */
class Seeder(seed: Long? = null) {

    private val random = if (seed != null) Random(seed) else Random()
    private val faker = Faker(Locale("es"), random)

    fun user(): FirestoreUserDto {
        val name = faker.name().fullName()
        return FirestoreUserDto(
            id = UUID.randomUUID().toString(),
            username = faker.name().username().take(MAX_USERNAME),
            name = name.take(MAX_NAME),
            email = faker.internet().emailAddress(),
            country = faker.address().country(),
            bio = faker.lorem().sentence(8).take(MAX_BIO),
            profileImage = null,
            createdAt = pastDate(daysBack = 720)
        )
    }

    fun product(): FirestoreProductDto = FirestoreProductDto(
        id = UUID.randomUUID().toString(),
        name = faker.commerce().productName(),
        category = CATEGORIES[random.nextInt(CATEGORIES.size)]
    )

    /** La reseña copia el autor y el producto, como lo hace la app al publicarla. */
    fun review(
        author: FirestoreUserDto,
        product: FirestoreProductDto,
        likers: List<FirestoreUserDto> = emptyList()
    ): FirestoreReviewDto {
        val rating = 1 + random.nextInt(5)
        return FirestoreReviewDto(
            id = UUID.randomUUID().toString(),
            userId = author.id,
            productId = product.id,
            productName = product.name,
            rating = rating,
            title = faker.lorem().sentence(3).trimEnd('.').take(MAX_TITLE),
            body = faker.lorem().paragraph(2).take(MAX_BODY),
            recommended = rating >= MIN_RECOMMENDED_RATING,
            user = FirestoreAuthorDto(name = author.name, username = author.username, profileImage = author.profileImage),
            likedBy = likers.filter { it.id != author.id }.map { it.id }.distinct(),
            createdAt = pastDate(daysBack = 90)
        )
    }

    /**
     * Las cantidades permiten generar volúmenes distintos. Toda referencia (autor, producto, likes,
     * seguidos) apunta a un documento generado aquí, y nadie se sigue ni se da like a sí mismo.
     */
    fun dataset(
        users: Int = 5,
        products: Int = 5,
        reviewsPerUser: Int = 2,
        followsPerUser: Int = 2,
        savedPerUser: Int = 2
    ): SeedDataset {
        require(users >= 0 && products >= 0 && reviewsPerUser >= 0 && followsPerUser >= 0) {
            "Las cantidades no pueden ser negativas"
        }
        require(products > 0 || reviewsPerUser == 0 || users == 0) {
            "Se necesita al menos un producto para generar reseñas"
        }
        val generatedUsers = List(users) { user() }
        val generatedProducts = List(products) { product() }
        val reviews = generatedUsers.flatMap { author ->
            List(reviewsPerUser) {
                review(
                    author,
                    generatedProducts[random.nextInt(generatedProducts.size)],
                    sample(generatedUsers, random.nextInt(MAX_LIKERS))
                )
            }
        }
        val follows = generatedUsers.flatMap { follower ->
            sample(generatedUsers.filter { it.id != follower.id }, followsPerUser).map {
                FirestoreFollowDto(followerId = follower.id, followedId = it.id, createdAt = pastDate(daysBack = 60))
            }
        }
        val saved = generatedUsers.flatMap { owner ->
            sample(generatedProducts, savedPerUser).map {
                FirestoreSavedDto(userId = owner.id, productId = it.id, createdAt = pastDate(daysBack = 30))
            }
        }
        return SeedDataset(generatedUsers, generatedProducts, reviews, follows, saved)
    }

    private fun <T> sample(list: List<T>, count: Int): List<T> = list.shuffled(random).take(count)

    private fun pastDate(daysBack: Int) = Date(System.currentTimeMillis() - random.nextInt(daysBack * 24) * HOUR_MS)

    private companion object {
        val CATEGORIES = listOf("Electrónica", "Hogar", "Deportes", "Libros", "Moda")
        const val HOUR_MS = 3_600_000L
        const val MIN_RECOMMENDED_RATING = 4
        const val MAX_LIKERS = 4
        const val MAX_USERNAME = 30
        const val MAX_NAME = 80
        const val MAX_BIO = 300
        const val MAX_TITLE = 120
        const val MAX_BODY = 2000
    }
}

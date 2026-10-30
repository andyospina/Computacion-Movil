package com.example.myapplication

import com.example.myapplication.debugtools.ProductionNotConfirmedException
import com.example.myapplication.debugtools.SeedLoader
import com.example.myapplication.debugtools.SeedTarget
import com.example.myapplication.debugtools.Seeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/** El Seeder no toca Firebase, así que sus reglas de coherencia se prueban en la JVM. */
class SeederTest {

    @Test
    fun dataset_respectsRequestedCounts() {
        val dataset = Seeder(seed = 1).dataset(users = 6, products = 3, reviewsPerUser = 4, followsPerUser = 2)

        assertEquals(6, dataset.users.size)
        assertEquals(3, dataset.products.size)
        assertEquals(24, dataset.reviews.size)
        assertEquals(12, dataset.follows.size)
    }

    @Test
    fun dataset_hasUniqueIds_andValidReferences() {
        val dataset = Seeder(seed = 2).dataset(users = 10, products = 4, reviewsPerUser = 3, followsPerUser = 3)
        val userIds = dataset.users.map { it.id }.toSet()
        val productIds = dataset.products.map { it.id }.toSet()

        assertEquals(10, userIds.size)
        assertEquals(30, dataset.reviews.map { it.id }.toSet().size)
        dataset.reviews.forEach { review ->
            assertTrue(review.userId in userIds)
            assertTrue(review.productId in productIds)
            assertTrue(review.likedBy.all { it in userIds && it != review.userId })
        }
        dataset.follows.forEach {
            assertTrue(it.followerId in userIds && it.followedId in userIds)
            assertFalse("nadie se sigue a sí mismo", it.followerId == it.followedId)
        }
    }

    @Test
    fun generatedValues_fitTheFirestoreRules() {
        val dataset = Seeder().dataset(users = 20, products = 5, reviewsPerUser = 2)

        dataset.users.forEach {
            assertTrue(it.name.isNotBlank() && it.name.length <= 80)
            assertTrue(it.username.isNotBlank() && it.username.length <= 30)
        }
        dataset.reviews.forEach {
            assertTrue(it.rating in 1..5)
            assertTrue(it.title.isNotBlank() && it.title.length <= 120)
            assertTrue(it.body.length <= 2000)
            assertEquals(it.rating >= 4, it.recommended)
        }
    }

    @Test
    fun dataset_withFixedSeed_isReproducible_exceptForIds() {
        val first = Seeder(seed = 5).dataset(users = 3, products = 2).users.map { it.name }
        val second = Seeder(seed = 5).dataset(users = 3, products = 2).users.map { it.name }

        assertEquals(first, second)
    }

    @Test
    fun dataset_rejectsReviewsWithoutProducts() {
        assertThrows(IllegalArgumentException::class.java) {
            Seeder().dataset(users = 2, products = 0, reviewsPerUser = 1)
        }
    }

    @Test
    fun productionTarget_requiresTheExactProjectId() {
        assertThrows(ProductionNotConfirmedException::class.java) {
            SeedLoader.requireConfirmed(SeedTarget.PRODUCTION, "mi-proyecto", confirmedProjectId = null)
        }
        assertThrows(ProductionNotConfirmedException::class.java) {
            SeedLoader.requireConfirmed(SeedTarget.PRODUCTION, "mi-proyecto", confirmedProjectId = "otro")
        }
        SeedLoader.requireConfirmed(SeedTarget.PRODUCTION, "mi-proyecto", confirmedProjectId = "mi-proyecto")
        SeedLoader.requireConfirmed(SeedTarget.EMULATOR, "mi-proyecto", confirmedProjectId = null)
    }
}

package com.example.myapplication

import com.example.myapplication.data.MAX_COMMENT_LENGTH
import com.example.myapplication.debugtools.Seeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Comentarios generados por el Faker (Sprint 15): coherentes con las reseñas y con las reglas. */
class SeederCommentsTest {

    @Test
    fun comments_pointToGeneratedReviewsAndUsers() {
        val dataset = Seeder(seed = 3).dataset(users = 8, products = 3, reviewsPerUser = 3)
        val reviewIds = dataset.reviews.map { it.id }.toSet()
        val userIds = dataset.users.map { it.id }.toSet()

        assertTrue("debe haber comentarios para probar la conversación", dataset.comments.isNotEmpty())
        assertTrue(dataset.comments.all { it.reviewId in reviewIds && it.userId in userIds })
        assertEquals(dataset.comments.size, dataset.comments.map { it.id }.toSet().size)
    }

    @Test
    fun comments_respectTheRulesLimitsAndComeAfterTheirReview() {
        val dataset = Seeder(seed = 4).dataset(users = 10, products = 4, reviewsPerUser = 3)
        val reviewsById = dataset.reviews.associateBy { it.id }

        dataset.comments.forEach { comment ->
            assertTrue(comment.text.isNotBlank() && comment.text.length <= MAX_COMMENT_LENGTH)
            assertTrue(comment.user.name.isNotBlank())
            assertTrue(!comment.createdAt!!.before(reviewsById.getValue(comment.reviewId).createdAt))
        }
    }

    @Test
    fun comments_canBeDisabled_andCountTowardsTheTotal() {
        val without = Seeder(seed = 5).dataset(users = 4, products = 2, maxCommentsPerReview = 0)
        val with = Seeder(seed = 5).dataset(users = 4, products = 2, maxCommentsPerReview = 3)

        assertTrue(without.comments.isEmpty())
        assertEquals(
            with.users.size + with.products.size + with.reviews.size + with.follows.size + with.saved.size +
                with.comments.size,
            with.totalDocuments
        )
    }
}

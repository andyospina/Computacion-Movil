package com.example.myapplication.datasource

import com.example.myapplication.data.datasource.impl.CommentFirestoreDataSource
import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreCommentDto
import com.example.myapplication.data.dto.NewCommentDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.CommentRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.data.datasource.impl.UserFirestoreDataSource
import com.example.myapplication.data.Result
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.Seeder
import com.example.myapplication.support.FirestoreEmulatorTest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/** Comentarios (Sprint 15): DataSource y Repository de Firestore contra el emulador. */
class CommentFirestoreDataSourceTest : FirestoreEmulatorTest() {

    private class FakeSession(var uid: String?) : CurrentUserIdProvider {
        override fun currentUserId(): String? = uid
    }

    private lateinit var dataSource: CommentFirestoreDataSource
    private val seeder = Seeder(seed = 31)

    @Before
    fun prepareDataSource() {
        dataSource = CommentFirestoreDataSource(firestore)
    }

    private fun newComment(reviewId: String, userId: String, text: String) =
        NewCommentDto(reviewId, userId, text, authorName = "Nombre $userId", authorUsername = userId, authorImage = null)

    @Test
    fun addComment_storesTheFieldsTheRulesExpect_withServerDate() = test {
        dataSource.addComment(newComment("r1", "ana", "¿Qué tal suena?"))

        val doc = firestore.collection("comments").get().await().documents.single()
        assertEquals(setOf("reviewId", "userId", "text", "user", "createdAt"), doc.data!!.keys)
        assertEquals("r1", doc.getString("reviewId"))
        assertEquals("ana", doc.getString("userId"))
        assertEquals("¿Qué tal suena?", doc.getString("text"))
        assertEquals("Nombre ana", doc.getString("user.name"))
        assertTrue("el servidor asigna createdAt", doc.getDate("createdAt") != null)
    }

    @Test
    fun observeComments_returnsOnlyThatReview_oldestFirst() = test {
        val now = System.currentTimeMillis()
        listOf("tercero" to 1L, "primero" to 3L, "segundo" to 2L).forEach { (id, minutesAgo) ->
            firestore.collection("comments").document(id).set(
                FirestoreCommentDto(
                    reviewId = "r1", userId = "ana", text = id, user = FirestoreAuthorDto(name = "Ana"),
                    createdAt = Date(now - minutesAgo * 60_000L)
                )
            ).await()
        }
        firestore.collection("comments").document("otra").set(
            FirestoreCommentDto(reviewId = "r2", userId = "ana", text = "otra", createdAt = Date(now))
        ).await()

        val ids = dataSource.observeComments("r1").first().map { (it as FirestoreCommentDto).id }

        assertEquals(listOf("primero", "segundo", "tercero"), ids)
    }

    @Test
    fun observeComments_emitsAgainWhenSomeoneComments() = test {
        val updates = async { dataSource.observeComments("r1").first { it.size == 2 } }
        dataSource.addComment(newComment("r1", "ana", "uno"))
        dataSource.addComment(newComment("r1", "luis", "dos"))

        assertEquals(listOf("uno", "dos"), updates.await().map { (it as FirestoreCommentDto).text })
    }

    @Test
    fun deleteComment_removesOnlyThatComment() = test {
        dataSource.addComment(newComment("r1", "ana", "se queda"))
        dataSource.addComment(newComment("r1", "ana", "se va"))
        val toDelete = dataSource.observeComments("r1").first()
            .map { it as FirestoreCommentDto }.single { it.text == "se va" }

        dataSource.deleteComment(toDelete.id)

        val remaining = dataSource.observeComments("r1").first().map { (it as FirestoreCommentDto).text }
        assertEquals(listOf("se queda"), remaining)
    }

    @Test
    fun repository_publishesWithTheRealProfileCopy_andMarksPermissions() = test {
        val author = seeder.user()
        val fan = seeder.user()
        val product = seeder.product()
        val review = seeder.review(author, product)
        seed(SeedDataset(listOf(author, fan), listOf(product), listOf(review), emptyList()))
        val session = FakeSession(fan.id)
        val repository = CommentRepository(dataSource, UserRepository(UserFirestoreDataSource(firestore)), session)

        assertTrue(repository.addComment(review.id, "  Gran reseña  ") is Result.Success)

        val asFan = (repository.observeComments(review.id, author.id).first() as Result.Success).data.single()
        assertEquals("Gran reseña", asFan.text)
        assertEquals(fan.name, asFan.authorName)
        assertTrue(asFan.isMine && asFan.canDelete)

        session.uid = author.id
        val asReviewAuthor = (repository.observeComments(review.id, author.id).first() as Result.Success).data.single()
        assertFalse(asReviewAuthor.isMine)
        assertTrue("el autor de la reseña modera su conversación", asReviewAuthor.canDelete)
    }
}

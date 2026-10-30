package com.example.myapplication.datasource

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.impl.ReviewFirestoreDataSource
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewData
import com.example.myapplication.data.dto.UpdateReviewDto
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.Seeder
import com.example.myapplication.support.FirestoreEmulatorTest
import com.example.myapplication.support.expectFailure
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/** Pruebas de integración de [ReviewFirestoreDataSource] contra Firebase Emulator Suite. */
class ReviewFirestoreDataSourceTest : FirestoreEmulatorTest() {

    private lateinit var dataSource: ReviewFirestoreDataSource
    private val seeder = Seeder(seed = 7)

    private val author = seeder.user()
    private val otherAuthor = seeder.user()
    private val product = seeder.product()
    private val otherProduct = seeder.product()

    @Before
    fun prepareDataSource() {
        dataSource = ReviewFirestoreDataSource(firestore)
        seed(SeedDataset(listOf(author, otherAuthor), listOf(product, otherProduct), emptyList(), emptyList()))
    }

    private fun newReview(title: String = "Muy buena") = NewReviewDto(
        productId = product.id,
        productName = product.name,
        userId = author.id,
        authorName = author.name,
        authorUsername = author.username,
        authorImage = null,
        rating = 5,
        title = title,
        body = "Cumple con lo prometido",
        recommended = true
    )

    private suspend fun storedReview(id: String) =
        firestore.collection("reviews").document(id).get().await()

    @Test
    fun createReview_storesTheDocumentInFirestore() = test {
        dataSource.createReview(newReview("Guardada"))

        val stored = firestore.collection("reviews").get().await().toObjects(FirestoreReviewDto::class.java)
        assertEquals(1, stored.size)
        assertEquals("Guardada", stored.single().title)
        assertEquals(author.id, stored.single().userId)
        assertEquals(author.name, stored.single().user.name)
        assertNotNull("El servidor debe asignar createdAt", stored.single().createdAt)
    }

    @Test
    fun getReviewById_returnsTheStoredReview() = test {
        val review = seeder.review(author, product).also { seed(SeedDataset(emptyList(), emptyList(), listOf(it), emptyList())) }

        val result = dataSource.getReviewById(review.id).toReview(currentUserId = null)

        assertEquals(review.id, result.id)
        assertEquals(review.title, result.title)
        assertEquals(review.rating, result.rating)
        assertEquals(product.name, result.productName)
    }

    @Test
    fun updateReview_changesOnlyTheEditableFields() = test {
        val review = seeder.review(author, product).copy(rating = 2, recommended = false)
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))

        dataSource.updateReview(review.id, UpdateReviewDto(rating = 5, title = "Editada", body = "Nuevo texto", recommended = true))

        val stored = storedReview(review.id).toObject(FirestoreReviewDto::class.java)!!
        assertEquals(5, stored.rating)
        assertEquals("Editada", stored.title)
        assertTrue(stored.recommended)
        assertEquals("autor y producto se conservan", review.userId, stored.userId)
        assertEquals(review.productId, stored.productId)
        assertEquals(review.createdAt, stored.createdAt)
    }

    @Test
    fun deleteReview_removesTheDocument() = test {
        val review = seeder.review(author, product).also { seed(SeedDataset(emptyList(), emptyList(), listOf(it), emptyList())) }

        dataSource.deleteReview(review.id)

        assertFalse(storedReview(review.id).exists())
        assertTrue(dataSource.getReviewsByProduct(product.id).isEmpty())
    }

    @Test
    fun getReviewsByProduct_returnsOnlyThatProductNewestFirst() = test {
        val now = System.currentTimeMillis()
        val old = seeder.review(author, product).copy(createdAt = Date(now - 3 * DAY))
        val recent = seeder.review(otherAuthor, product).copy(createdAt = Date(now - DAY))
        val elsewhere = seeder.review(author, otherProduct)
        seed(SeedDataset(emptyList(), emptyList(), listOf(old, recent, elsewhere), emptyList()))

        val result = dataSource.getReviewsByProduct(product.id).map { it.toReview(null).id }

        assertEquals(listOf(recent.id, old.id), result)
    }

    @Test
    fun getReviewById_withUnknownId_throwsNotFound() = test {
        val error = expectFailure<NotFoundException> { dataSource.getReviewById("no-existe") }

        assertTrue(error.message!!.contains("no-existe"))
    }

    @Test
    fun getReviewById_withIncompleteDocument_fillsDefaults_andWithWrongTypes_fails() = test {
        // Faltan campos obligatorios: la deserialización los completa con valores por defecto.
        firestore.collection("reviews").document("incompleto").set(mapOf("userId" to author.id)).await()
        val incomplete = dataSource.getReviewById("incompleto").toReview(null)
        assertEquals(0, incomplete.rating)
        assertEquals("", incomplete.title)

        // Un tipo inválido (rating como texto) no se puede convertir y debe fallar, no devolver basura.
        firestore.collection("reviews").document("tipo-malo").set(mapOf("rating" to "cinco")).await()
        expectFailure<RuntimeException> { dataSource.getReviewById("tipo-malo") }
    }

    @Test
    fun updateReview_onMissingDocument_failsWithNotFound() = test {
        val error = expectFailure<FirebaseFirestoreException> {
            dataSource.updateReview("fantasma", UpdateReviewDto(rating = 3, title = "x", body = "y", recommended = false))
        }

        assertEquals(FirebaseFirestoreException.Code.NOT_FOUND, error.code)
    }

    @Test
    fun concurrentLikes_fromManyUsers_areAllKept() = test {
        val review = seeder.review(author, product).also { seed(SeedDataset(emptyList(), emptyList(), listOf(it), emptyList())) }

        coroutineScope {
            (1..20).map { n -> async { dataSource.likeReview(review.id, "usuario-$n") } }.awaitAll()
        }

        val likedBy = storedReview(review.id).toObject(FirestoreReviewDto::class.java)!!.likedBy
        assertEquals(20, likedBy.size)
        assertEquals(20, likedBy.toSet().size)
    }

    @Test
    fun concurrentCreates_storeEveryReview() = test {
        coroutineScope {
            (1..10).map { n -> async { dataSource.createReview(newReview("Reseña $n")) } }.awaitAll()
        }

        val titles = dataSource.getReviewsByProduct(product.id).map { it.toReview(null).title }
        assertEquals((1..10).map { "Reseña $it" }.toSet(), titles.toSet())
    }

    @Test
    fun likeAndUnlike_areIdempotentPerUser() = test {
        val review = seeder.review(author, product).copy(likedBy = emptyList())
        seed(SeedDataset(emptyList(), emptyList(), listOf(review), emptyList()))

        dataSource.likeReview(review.id, "ana")
        dataSource.likeReview(review.id, "ana")
        assertTrue(dataSource.getReviewById(review.id).toReview("ana").likedByMe)
        assertEquals(1, dataSource.getReviewById(review.id).toReview(null).likesCount)

        dataSource.unlikeReview(review.id, "ana")
        assertEquals(0, dataSource.getReviewById(review.id).toReview(null).likesCount)
    }

    @Test
    fun observeReviewsByProduct_emitsWhenAReviewIsAdded() = test {
        val emissions = Channel<List<ReviewData>>(Channel.UNLIMITED)
        val listener = launch { dataSource.observeReviewsByProduct(product.id).collect { emissions.send(it) } }

        assertTrue("la primera emisión es el estado inicial vacío", emissions.receive().isEmpty())
        dataSource.createReview(newReview("En vivo"))

        var updated = emissions.receive()
        while (updated.isEmpty()) updated = emissions.receive()
        assertEquals("En vivo", updated.single().toReview(null).title)
        listener.cancel()
    }

    private companion object {
        const val DAY = 24 * 3_600_000L
    }
}

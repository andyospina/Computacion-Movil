package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.impl.ProductFirestoreDataSource
import com.example.myapplication.data.datasource.impl.SavedFirestoreDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.NotAuthenticatedException
import com.example.myapplication.data.repository.SavedRepository
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.Seeder
import com.example.myapplication.support.FirestoreEmulatorTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** SavedRepository: DataSources de Firestore reales contra el emulador. */
class SavedRepositoryIntegrationTest : FirestoreEmulatorTest() {

    private class FakeSession(var uid: String?) : CurrentUserIdProvider {
        override fun currentUserId(): String? = uid
    }

    private val seeder = Seeder(seed = 31)
    private val author = seeder.user()
    private val products = List(3) { seeder.product() }
    private val session = FakeSession("ana")

    private lateinit var repository: SavedRepository

    @Before
    fun prepare() {
        repository = SavedRepository(SavedFirestoreDataSource(firestore), ProductFirestoreDataSource(firestore), session)
        seed(SeedDataset(listOf(author), products, emptyList(), emptyList()))
    }

    private fun Result<List<Product>>.ids() = ((this as Result.Success).data).map { it.id }

    @Test
    fun observeSavedProducts_mapsToProducts_newestSavedFirst() = test {
        repository.setSaved(products[0].id, true)
        repository.setSaved(products[2].id, true)

        val result = repository.observeSavedProducts().first { it is Result.Success && it.data.size == 2 }

        assertEquals(setOf(products[0].id, products[2].id), result.ids().toSet())
        assertEquals(products[2].id, result.ids().first())
        assertEquals(products[2].name, (result as Result.Success).data.first().name)
    }

    @Test
    fun observeSavedProducts_reflectsSaveAndUnsaveLive() = test {
        val first = repository.observeSavedProducts().first()
        assertTrue((first as Result.Success).data.isEmpty())

        repository.setSaved(products[1].id, true)
        assertEquals(listOf(products[1].id), repository.observeSavedProducts().first { it is Result.Success && it.data.isNotEmpty() }.ids())

        repository.setSaved(products[1].id, false)
        assertTrue(repository.observeSavedProducts().first { it is Result.Success && it.data.isEmpty() }.ids().isEmpty())
    }

    @Test
    fun observeSavedProducts_skipsProductsThatNoLongerExist() = test {
        repository.setSaved(products[0].id, true)
        repository.setSaved("producto-borrado", true)

        val result = repository.observeSavedProducts().first { it is Result.Success && it.data.isNotEmpty() }

        assertEquals(listOf(products[0].id), result.ids())
    }

    @Test
    fun observeIsSaved_followsTheButtonState() = test {
        assertFalse((repository.observeIsSaved(products[0].id).first() as Result.Success).data)

        repository.setSaved(products[0].id, true)

        val saved = repository.observeIsSaved(products[0].id).first { it is Result.Success && it.data }
        assertTrue((saved as Result.Success).data)
        assertFalse("otro producto no queda marcado", (repository.observeIsSaved(products[1].id).first() as Result.Success).data)
    }

    @Test
    fun savedProducts_arePerUser() = test {
        repository.setSaved(products[0].id, true)
        session.uid = "luis"

        val result = repository.observeSavedProducts().first()

        assertTrue(result.ids().isEmpty())
    }

    @Test
    fun setSaved_withoutSession_failsAndWritesNothing() = test {
        session.uid = null

        val result = repository.setSaved(products[0].id, true) as Result.Failure

        assertTrue(result.exception is NotAuthenticatedException)
        assertEquals(R.string.error_session_required, result.messageRes)
        assertTrue(firestore.collection("saved").get().await().isEmpty)
    }
}

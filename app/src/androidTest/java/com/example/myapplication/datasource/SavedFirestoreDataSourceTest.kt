package com.example.myapplication.datasource

import com.example.myapplication.data.datasource.impl.SavedFirestoreDataSource
import com.example.myapplication.data.dto.FirestoreSavedDto
import com.example.myapplication.support.FirestoreEmulatorTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/** Productos guardados: DataSource de Firestore contra el emulador. */
class SavedFirestoreDataSourceTest : FirestoreEmulatorTest() {

    private lateinit var dataSource: SavedFirestoreDataSource

    @Before
    fun prepareDataSource() {
        dataSource = SavedFirestoreDataSource(firestore)
    }

    @Test
    fun save_createsOneDocumentPerUserAndProduct() = test {
        dataSource.save("ana", "p1")

        val doc = firestore.collection("saved").document("ana_p1").get().await()
        assertTrue(doc.exists())
        assertEquals("ana", doc.getString("userId"))
        assertEquals("p1", doc.getString("productId"))
        assertTrue("el servidor asigna createdAt", doc.getDate("createdAt") != null)
    }

    @Test
    fun save_twice_doesNotDuplicate() = test {
        dataSource.save("ana", "p1")
        dataSource.save("ana", "p1")

        assertEquals(1, firestore.collection("saved").get().await().size())
        assertEquals(listOf("p1"), dataSource.observeSavedProductIds("ana").first())
    }

    @Test
    fun unsave_removesOnlyThatProduct() = test {
        dataSource.save("ana", "p1")
        dataSource.save("ana", "p2")

        dataSource.unsave("ana", "p1")

        assertEquals(listOf("p2"), dataSource.observeSavedProductIds("ana").first())
        assertFalse(firestore.collection("saved").document("ana_p1").get().await().exists())
    }

    @Test
    fun observeSavedProductIds_returnsNewestFirst() = test {
        val now = System.currentTimeMillis()
        listOf("viejo" to 3L, "medio" to 2L, "nuevo" to 1L).forEach { (product, daysAgo) ->
            firestore.collection("saved").document("ana_$product")
                .set(FirestoreSavedDto("ana", product, Date(now - daysAgo * 86_400_000L))).await()
        }

        assertEquals(listOf("nuevo", "medio", "viejo"), dataSource.observeSavedProductIds("ana").first())
    }

    @Test
    fun observeSavedProductIds_isolatesUsers() = test {
        dataSource.save("ana", "p1")
        dataSource.save("luis", "p2")

        assertEquals(listOf("p1"), dataSource.observeSavedProductIds("ana").first())
        assertTrue(dataSource.observeSavedProductIds("nadie").first().isEmpty())
    }
}

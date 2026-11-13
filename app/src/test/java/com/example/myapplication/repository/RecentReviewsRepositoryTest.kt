package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.MAX_MAP_REVIEWS
import com.example.myapplication.data.repository.RECENT_WINDOW_MILLIS
import com.example.myapplication.data.repository.RecentReviewsRepository
import com.example.myapplication.data.time.TimeProvider
import com.google.firebase.firestore.GeoPoint
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/** [RecentReviewsRepository] con DataSource y reloj simulados: la regla de las 24 h se prueba sin esperar. */
class RecentReviewsRepositoryTest {

    private val remote = mockk<ReviewRemoteDataSource>()
    private val session = mockk<CurrentUserIdProvider>()
    private var now = 1_800_000_000_000L
    private val clock = object : TimeProvider {
        override fun nowMillis() = now
    }
    private val repository = RecentReviewsRepository(remote, session, clock)

    private val bogota = GeoPoint(4.711, -74.072)

    private fun dto(id: String, ageMillis: Long?, location: GeoPoint? = bogota) = FirestoreReviewDto(
        id = id, userId = "u1", productId = "p1", productName = "Audífonos", rating = 4, title = "Título $id",
        body = "Cuerpo", recommended = true, user = FirestoreAuthorDto(name = "Ana", username = "ana"),
        location = location, createdAt = ageMillis?.let { Date(now - it) }
    )

    private fun stub(vararg dtos: FirestoreReviewDto) {
        every { session.currentUserId() } returns "me"
        every { remote.observeReviewsSince(any(), any()) } returns flowOf(dtos.toList())
    }

    private suspend fun firstReviews(): List<Review> =
        (repository.observeRecentLocatedReviews().first() as Result.Success).data

    @Test
    fun asksTheDataSourceForTheLast24HoursWithALimit() = runTest {
        stub()

        firstReviews()

        verify { remote.observeReviewsSince(now - RECENT_WINDOW_MILLIS, MAX_MAP_REVIEWS) }
    }

    @Test
    fun keepsOnlyReviewsThatHaveALocation() = runTest {
        stub(dto("con", ageMillis = 1_000), dto("sin", ageMillis = 1_000, location = null))

        assertEquals(listOf("con"), firstReviews().map { it.id })
    }

    @Test
    fun dropsReviewsOlderThan24Hours() = runTest {
        stub(
            dto("hace-1h", ageMillis = 3_600_000),
            dto("hace-23h59", ageMillis = RECENT_WINDOW_MILLIS - 60_000),
            dto("hace-25h", ageMillis = RECENT_WINDOW_MILLIS + 3_600_000)
        )

        assertEquals(listOf("hace-1h", "hace-23h59"), firstReviews().map { it.id })
    }

    @Test
    fun aReviewWithoutServerDateYet_isTreatedAsJustPublished() = runTest {
        stub(dto("recien-creada", ageMillis = null))

        assertEquals(listOf("recien-creada"), firstReviews().map { it.id })
    }

    @Test
    fun mapsLocationAndDateToTheDomainModel() = runTest {
        stub(dto("r1", ageMillis = 5_000))

        val review = firstReviews().single()

        assertEquals(4.711, review.location!!.latitude, 0.0)
        assertEquals(-74.072, review.location!!.longitude, 0.0)
        assertEquals(now - 5_000, review.createdAtMillis)
    }

    @Test
    fun aReviewLeavesTheMapWhenItTurns24HoursOldWhileTheScreenIsOpen() = runTest {
        // Tiene 23 h: entra. Cuando la pantalla sigue abierta y pasan 2 h más, el refresco la descarta.
        stub(dto("va-a-vencer", ageMillis = 23 * 3_600_000L))
        val seen = mutableListOf<List<String>>()

        repository.observeRecentLocatedReviews().take(2).collect { result ->
            seen += (result as Result.Success).data.map { it.id }
            now += 2 * 3_600_000L
        }

        assertEquals(listOf(listOf("va-a-vencer"), emptyList<String>()), seen)
    }

    @Test
    fun backendWithoutLocationSupport_failsWithTheNotSupportedMessage() = runTest {
        every { session.currentUserId() } returns "me"
        every { remote.observeReviewsSince(any(), any()) } returns
            flow { throw UnsupportedOperationException("sin ubicación") }

        val result = repository.observeRecentLocatedReviews().first()

        assertTrue(result is Result.Failure)
        assertEquals(R.string.error_not_supported_backend, (result as Result.Failure).messageRes)
    }
}

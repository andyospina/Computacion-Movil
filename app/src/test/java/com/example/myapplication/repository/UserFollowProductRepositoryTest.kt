package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.FollowRemoteDataSource
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.dto.FirestoreProductData
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.FirestoreUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.Calendar
import java.util.GregorianCalendar

/** Pruebas con DataSource simulado de [UserRepository], [FollowRepository] y [ProductRepository]. */
class UserFollowProductRepositoryTest {

    private val userSource = mockk<UserRemoteDataSource>()
    private val followSource = mockk<FollowRemoteDataSource>()
    private val productSource = mockk<ProductRemoteDataSource>()
    private val session = mockk<CurrentUserIdProvider>()

    private val users = UserRepository(userSource)
    private val follows = FollowRepository(followSource, session)
    private val products = ProductRepository(productSource)

    private fun userDto(id: String, name: String = "Ada Lovelace") = FirestoreUserDto(
        id = id, username = "user_$id", name = name, email = "$id@x.com", country = "UK", bio = "Matemática",
        createdAt = GregorianCalendar(2024, Calendar.JUNE, 15).time
    )

    // ---- UserRepository (mapeos) ----

    @Test
    fun getUserById_mapsDtoToUserWithInitialsAndMemberSinceYear() = runTest {
        coEvery { userSource.getUserById("u1") } returns userDto("u1")

        val user = (users.getUserById("u1") as Result.Success).data

        assertEquals("Ada Lovelace", user.name)
        assertEquals("AL", user.initials)
        assertEquals("user_u1", user.username)
        assertEquals("Matemática", user.bio)
        assertEquals("UK", user.country)
        assertEquals(2024, user.memberSinceYear)
        assertEquals("u1", user.id)
    }

    @Test
    fun getUsersByIds_keepsTheRequestedOrderAndOmitsMissingUsers() = runTest {
        // El DataSource devuelve en otro orden y sin "fantasma": el Repository ordena por la lista pedida
        coEvery { userSource.getUsersByIds(listOf("b", "fantasma", "a")) } returns listOf(userDto("a"), userDto("b"))

        val result = (users.getUsersByIds(listOf("b", "fantasma", "a")) as Result.Success).data

        assertEquals(listOf("b", "a"), result.map { it.id })
    }

    @Test
    fun getUsersByIds_withEmptyList_doesNotCallTheDataSource() = runTest {
        val result = (users.getUsersByIds(emptyList()) as Result.Success).data

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { userSource.getUsersByIds(any()) }
    }

    @Test
    fun updateUser_trimsTheFieldsBeforeSendingThem_andNetworkErrorsBecomeFailures() = runTest {
        val sent = slot<UpdateUserDto>()
        coEvery { userSource.updateUser("u1", capture(sent)) } returns Unit
        users.updateUser("u1", name = "  Ada ", country = " UK ", bio = " hola ")
        assertEquals(UpdateUserDto("Ada", "UK", "hola"), sent.captured)

        coEvery { userSource.getUserById("u2") } throws IOException("sin red")
        val failure = users.getUserById("u2") as Result.Failure
        assertEquals(R.string.error_api_unreachable, failure.messageRes)
    }

    // ---- FollowRepository ----

    @Test
    fun follow_usesTheSessionUserAsFollower() = runTest {
        every { session.currentUserId() } returns "me"
        coEvery { followSource.follow("me", "u2") } returns Unit

        assertTrue(follows.follow("u2") is Result.Success)

        coVerify(exactly = 1) { followSource.follow(followerId = "me", followedId = "u2") }
    }

    @Test
    fun follow_oneself_failsAndNeverCallsTheDataSource() = runTest {
        every { session.currentUserId() } returns "me"

        assertTrue(follows.follow("me") is Result.Failure)

        coVerify(exactly = 0) { followSource.follow(any(), any()) }
    }

    @Test
    fun unfollow_withoutSession_failsWithSessionRequiredMessage() = runTest {
        every { session.currentUserId() } returns null

        val failure = follows.unfollow("u2") as Result.Failure

        assertEquals(R.string.error_session_required, failure.messageRes)
    }

    @Test
    fun observeFollowStats_combinesFollowersAndFollowing() = runTest {
        every { followSource.observeFollowerIds("u2") } returns flowOf(listOf("a", "b"))
        every { followSource.observeFollowingIds("u2") } returns flowOf(listOf("c"))

        val stats = (follows.observeFollowStats("u2").first() as Result.Success).data

        assertEquals(2, stats.followersCount)
        assertEquals(1, stats.followingCount)
        assertTrue(stats.isFollowedBy("a"))
    }

    // ---- ProductRepository (mapeo con estadísticas) ----

    @Test
    fun getProducts_computesAverageRatingAndRecommendedPercentFromItsReviews() = runTest {
        val reviews = listOf(5 to true, 3 to false).map { (rating, recommended) ->
            FirestoreReviewDto(rating = rating, recommended = recommended)
        }
        coEvery { productSource.getProducts() } returns
            listOf(FirestoreProductData(FirestoreProductDto("p1", "Audífonos", "Electrónica"), reviews))

        val product = (products.getProducts() as Result.Success).data.single()

        assertEquals(4.0, product.rating, 0.001)
        assertEquals(2, product.reviewCount)
        assertEquals(50, product.recommendPercent)
    }
}

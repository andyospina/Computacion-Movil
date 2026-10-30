package com.example.myapplication.datasource

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.impl.FollowFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ProductFirestoreDataSource
import com.example.myapplication.data.datasource.impl.UserFirestoreDataSource
import com.example.myapplication.data.dto.RegisterUserDto
import com.example.myapplication.data.dto.UpdateUserDto
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.debugtools.Seeder
import com.example.myapplication.support.FirestoreEmulatorTest
import com.example.myapplication.support.expectFailure
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** DataSources de usuarios, productos y seguidores contra el emulador. */
class UserProductFollowDataSourceTest : FirestoreEmulatorTest() {

    private val seeder = Seeder(seed = 11)

    @Test
    fun registerUser_thenGetUserById_returnsTheSavedProfile() = test {
        val users = UserFirestoreDataSource(firestore)

        users.registerUser("uid-1", RegisterUserDto(username = "ana", name = "Ana Pérez", email = "ana@example.com", country = "Colombia"))

        val user = users.getUserById("uid-1").toUser()
        assertEquals("Ana Pérez", user.name)
        assertEquals("ana", user.username)
        assertEquals("uid-1", user.id)
    }

    @Test
    fun updateUser_keepsUsernameAndEmail_andGetUserById_withUnknownIdFails() = test {
        val users = UserFirestoreDataSource(firestore)
        users.registerUser("uid-2", RegisterUserDto(username = "luis", name = "Luis", email = "luis@example.com"))

        users.updateUser("uid-2", UpdateUserDto(name = "Luis Gómez", country = "Perú", bio = "Hola"))

        val updated = users.getUserById("uid-2").toUser()
        assertEquals("Luis Gómez", updated.name)
        assertEquals("luis", updated.username)
        expectFailure<NotFoundException> { users.getUserById("nadie") }
    }

    @Test
    fun getUsersByIds_skipsMissingUsers_andSupportsMoreThanThePerQueryLimit() = test {
        val dataset = seeder.dataset(users = 35, products = 0, reviewsPerUser = 0, followsPerUser = 0)
        seed(dataset)
        val ids = dataset.users.map { it.id } + "no-existe"

        val found = UserFirestoreDataSource(firestore).getUsersByIds(ids)

        assertEquals(35, found.size)
    }

    @Test
    fun getProducts_computesRatingFromTheirReviews() = test {
        val author = seeder.user()
        val product = seeder.product()
        val empty = seeder.product()
        val reviews = listOf(4, 5).map { seeder.review(author, product).copy(rating = it) }
        seed(SeedDataset(listOf(author), listOf(product, empty), reviews, emptyList()))

        val byId = ProductFirestoreDataSource(firestore).getProducts().map { it.toProduct() }.associateBy { it.id }

        assertEquals(4.5, byId.getValue(product.id).rating, 0.001)
        assertEquals(0.0, byId.getValue(empty.id).rating, 0.001)
    }

    @Test
    fun getProductById_withUnknownId_throwsNotFound() = test {
        expectFailure<NotFoundException> { ProductFirestoreDataSource(firestore).getProductById("no-existe") }
    }

    @Test
    fun followAndUnfollow_updateBothDirections() = test {
        val follows = FollowFirestoreDataSource(firestore)

        follows.follow(followerId = "ana", followedId = "luis")
        follows.follow(followerId = "ana", followedId = "luis") // mismo documento: no duplica

        assertEquals(listOf("ana"), follows.observeFollowerIds("luis").first())
        assertEquals(listOf("luis"), follows.observeFollowingIds("ana").first())

        follows.unfollow(followerId = "ana", followedId = "luis")
        assertTrue(follows.observeFollowerIds("luis").first().isEmpty())
    }
}

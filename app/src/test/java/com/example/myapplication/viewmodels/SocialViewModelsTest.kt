package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.FollowStats
import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.FollowRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.data.repository.SavedRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.ui.screens.Feed.FeedViewModel
import com.example.myapplication.ui.screens.LikedReviews.LikedReviewsViewModel
import com.example.myapplication.ui.screens.ProductDetail.ProductDetailViewModel
import com.example.myapplication.ui.screens.UserProfile.UserProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** ViewModels del perfil de usuario, feed «Siguiendo», «Me gustaron» y ficha de producto con Repository simulado. */
class SocialViewModelsTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val reviews = mockk<ReviewRepository>()
    private val follows = mockk<FollowRepository>()
    private val users = mockk<UserRepository>()
    private val session = mockk<CurrentUserIdProvider>()

    private fun review(id: String, userId: String = "u2", likedByMe: Boolean = false) = Review(
        id = id, productId = "p1", author = "Ana", verifiedBuyer = false, rating = 5, title = "Título $id",
        body = "Cuerpo", date = "", usefulCount = 0, recommended = true, userId = userId,
        productName = "Audífonos", likesCount = if (likedByMe) 1 else 0, likedByMe = likedByMe
    )

    private val ana = User(
        name = "Ana Pérez", initials = "AP", memberSinceYear = 2024, reviewCount = 0, usefulVotes = 0,
        averageRating = 0.0, username = "ana", bio = "Bio de Ana", country = "Colombia", id = "u2"
    )

    private fun profileViewModel(me: String?, stats: FollowStats = FollowStats()): UserProfileViewModel {
        every { session.currentUserId() } returns me
        coEvery { users.getUserById("u2") } returns Result.Success(ana)
        every { reviews.observeReviewsByUsers(listOf("u2")) } returns flowOf(Result.Success(emptyList()))
        every { follows.observeFollowStats("u2") } returns flowOf(Result.Success(stats))
        return UserProfileViewModel(users, reviews, follows, session).also { it.getPerfil("u2") }
    }

    // ---------------- UserProfileViewModel ----------------

    @Test
    fun userProfile_loadsTheUserTheFollowersAndWhetherIFollowThem() {
        val vm = profileViewModel(me = "me", stats = FollowStats(followerIds = listOf("me", "otro")))

        val state = vm.uiState.value
        assertEquals("Ana Pérez", state.user?.name)
        assertEquals(2, state.followStats.followersCount)
        assertTrue(state.siguiendo)
        assertFalse(state.esMiPerfil)
    }

    @Test
    fun userProfile_notFollowing_toggleFollowCallsFollow() {
        val vm = profileViewModel(me = "me")
        coEvery { follows.follow("u2") } returns Result.Success(Unit)

        vm.toggleSeguir()

        coVerify(exactly = 1) { follows.follow("u2") }
        coVerify(exactly = 0) { follows.unfollow(any()) }
        assertFalse(vm.uiState.value.isFollowLoading)
    }

    @Test
    fun userProfile_alreadyFollowing_toggleFollowCallsUnfollow() {
        val vm = profileViewModel(me = "me", stats = FollowStats(followerIds = listOf("me")))
        coEvery { follows.unfollow("u2") } returns Result.Success(Unit)

        vm.toggleSeguir()

        coVerify(exactly = 1) { follows.unfollow("u2") }
        coVerify(exactly = 0) { follows.follow(any()) }
    }

    @Test
    fun userProfile_ownProfile_cannotBeFollowed() {
        val vm = profileViewModel(me = "u2")

        vm.toggleSeguir()

        assertTrue(vm.uiState.value.esMiPerfil)
        coVerify(exactly = 0) { follows.follow(any()) }
    }

    @Test
    fun userProfile_followFailure_exposesTheActionError() {
        val vm = profileViewModel(me = "me")
        coEvery { follows.follow("u2") } returns Result.Failure(Exception("x"), R.string.error_permission_denied)

        vm.toggleSeguir()
        assertEquals(R.string.error_permission_denied, vm.uiState.value.actionErrorRes)

        vm.onActionErrorShown()
        assertNull(vm.uiState.value.actionErrorRes)
    }

    // ---------------- FeedViewModel ----------------

    @Test
    fun feed_showsOnlyTheReviewsOfTheUsersIFollow() {
        every { follows.observeMyFollowingIds() } returns flowOf(Result.Success(listOf("u2", "u3")))
        every { reviews.observeReviewsByUsers(listOf("u2", "u3")) } returns
            flowOf(Result.Success(listOf(review("r1", "u2"), review("r2", "u3"))))

        val vm = FeedViewModel(follows, reviews)

        assertEquals(listOf("r1", "r2"), vm.uiState.value.resenas.map { it.id })
        assertEquals(2, vm.uiState.value.cantidadSeguidos)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun feed_followingNobody_isEmptyWithZeroFollowed() {
        every { follows.observeMyFollowingIds() } returns flowOf(Result.Success(emptyList()))
        every { reviews.observeReviewsByUsers(emptyList()) } returns flowOf(Result.Success(emptyList()))

        val vm = FeedViewModel(follows, reviews)

        assertTrue(vm.uiState.value.resenas.isEmpty())
        assertEquals(0, vm.uiState.value.cantidadSeguidos)
    }

    // ---------------- LikedReviewsViewModel (funcionalidad adicional) ----------------

    @Test
    fun likedReviews_listUpdatesLiveWhenALikeIsRemoved() {
        val liveList = MutableStateFlow<Result<List<Review>>>(
            Result.Success(listOf(review("r1", likedByMe = true), review("r2", likedByMe = true)))
        )
        every { reviews.observeMyLikedReviews() } returns liveList
        val vm = LikedReviewsViewModel(reviews)
        assertEquals(listOf("r1", "r2"), vm.uiState.value.resenas.map { it.id })

        liveList.value = Result.Success(listOf(review("r2", likedByMe = true)))

        assertEquals(listOf("r2"), vm.uiState.value.resenas.map { it.id })
    }

    @Test
    fun likedReviews_withoutSession_showsTheErrorAndNoList() {
        every { reviews.observeMyLikedReviews() } returns
            flowOf(Result.Failure(Exception("sin sesión"), R.string.error_session_required))

        val vm = LikedReviewsViewModel(reviews)

        assertEquals(R.string.error_session_required, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun likedReviews_toggleLikeCallsTheRepositoryAndReportsFailures() {
        every { reviews.observeMyLikedReviews() } returns flowOf(Result.Success(emptyList()))
        val liked = review("r1", likedByMe = true)
        coEvery { reviews.toggleLike(liked) } returns Result.Failure(Exception("x"), R.string.error_api_unreachable)
        val vm = LikedReviewsViewModel(reviews)

        vm.toggleLike(liked)

        coVerify(exactly = 1) { reviews.toggleLike(liked) }
        assertEquals(R.string.error_api_unreachable, vm.uiState.value.actionErrorRes)
    }

    // ---------------- ProductDetailViewModel ----------------

    private fun productDetailViewModel(saved: Boolean): Pair<ProductDetailViewModel, SavedRepository> {
        val products = mockk<ProductRepository>()
        val savedRepository = mockk<SavedRepository>()
        coEvery { products.getProductById("p1") } returns Result.Success(
            Product("p1", "Audífonos", "Electrónica", 4.0, 2, 50, emptyList())
        )
        every { reviews.observeReviewsByProduct("p1") } returns
            flowOf(Result.Success(listOf(review("r1"), review("r2"), review("r3"))))
        every { savedRepository.observeIsSaved("p1") } returns flowOf(Result.Success(saved))
        val vm = ProductDetailViewModel(products, reviews, savedRepository)
        vm.getProducto("p1")
        return vm to savedRepository
    }

    @Test
    fun productDetail_loadsTheProductAndOnlyTheTwoFeaturedReviews() {
        val (vm, _) = productDetailViewModel(saved = true)

        assertEquals("Audífonos", vm.uiState.value.producto?.name)
        assertEquals(2, vm.uiState.value.resenasDestacadas.size)
        assertTrue(vm.uiState.value.guardado)
    }

    @Test
    fun productDetail_toggleGuardado_savesWhenNotSavedYet() {
        val (vm, savedRepository) = productDetailViewModel(saved = false)
        coEvery { savedRepository.setSaved("p1", true) } returns Result.Success(Unit)

        vm.toggleGuardado()

        coVerify(exactly = 1) { savedRepository.setSaved("p1", true) }
    }
}

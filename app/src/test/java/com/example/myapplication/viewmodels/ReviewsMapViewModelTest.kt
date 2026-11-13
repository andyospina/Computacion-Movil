package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.RecentReviewsRepository
import com.example.myapplication.ui.screens.Map.ReviewsMapViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReviewsMapViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val recent = mockk<RecentReviewsRepository>()

    private fun review(id: String) = Review(
        id = id, productId = "p1", author = "Ana", verifiedBuyer = false, rating = 4, title = "Título $id",
        body = "Cuerpo", date = "", usefulCount = 0, recommended = true, userId = "u1", productName = "Audífonos",
        location = GeoLocation(4.7, -74.0), createdAtMillis = 1L
    )

    @Test
    fun showsTheRecentReviewsAndStopsLoading() {
        every { recent.observeRecentLocatedReviews() } returns flowOf(Result.Success(listOf(review("a"), review("b"))))

        val vm = ReviewsMapViewModel(recent)

        assertEquals(listOf("a", "b"), vm.uiState.value.reviews.map { it.id })
        assertFalse(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.selected)
    }

    @Test
    fun tappingAMarker_selectsItsReview_andTappingTheMapClosesIt() {
        every { recent.observeRecentLocatedReviews() } returns flowOf(Result.Success(listOf(review("a"), review("b"))))
        val vm = ReviewsMapViewModel(recent)

        vm.onMarcadorClick("b")
        assertEquals("b", vm.uiState.value.selected?.id)

        vm.onSeleccionCerrada()
        assertNull(vm.uiState.value.selected)
    }

    @Test
    fun theSelectedReviewIsDropped_whenItLeavesTheLast24Hours() {
        val updates = MutableSharedFlow<Result<List<Review>>>(replay = 1)
        updates.tryEmit(Result.Success(listOf(review("a"), review("b"))))
        every { recent.observeRecentLocatedReviews() } returns updates
        val vm = ReviewsMapViewModel(recent)
        vm.onMarcadorClick("a")

        updates.tryEmit(Result.Success(listOf(review("b"))))

        assertNull("la tarjeta no debe mostrar una reseña que ya no está en el mapa", vm.uiState.value.selected)
        assertEquals(listOf("b"), vm.uiState.value.reviews.map { it.id })
    }

    @Test
    fun anEmptyWindow_isNotAnError() {
        every { recent.observeRecentLocatedReviews() } returns flowOf(Result.Success(emptyList()))

        val vm = ReviewsMapViewModel(recent)

        assertTrue(vm.uiState.value.reviews.isEmpty())
        assertNull(vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun aFailure_showsTheErrorAndRetryResubscribes() {
        every { recent.observeRecentLocatedReviews() } returnsMany listOf(
            flowOf(Result.Failure(RuntimeException("sin red"), R.string.error_api_unreachable)),
            flowOf(Result.Success(listOf(review("a"))))
        )

        val vm = ReviewsMapViewModel(recent)
        assertEquals(R.string.error_api_unreachable, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.isLoading)

        vm.reintentar()

        assertNull(vm.uiState.value.errorMessageRes)
        assertEquals(listOf("a"), vm.uiState.value.reviews.map { it.id })
    }
}

package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.ReportReason
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.ReportRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.ui.screens.ReviewDetail.ReviewDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/** Detalle de reseña: carga y reporte (solo reseñas ajenas, con diálogo y estado «ya reportada»). */
class ReviewDetailViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val reviews = mockk<ReviewRepository>()
    private val reports = mockk<ReportRepository>()
    private val session = mockk<CurrentUserIdProvider>()

    private val review = Review(
        id = "r1", productId = "p1", author = "Bob", verifiedBuyer = false, rating = 5, title = "Excelentes",
        body = "Cuerpo", date = "", usefulCount = 0, recommended = true, userId = "bob", productName = "Audífonos"
    )

    private fun loaded(me: String? = "ana", alreadyReported: Boolean = false): ReviewDetailViewModel {
        every { session.currentUserId() } returns me
        coEvery { reviews.getReviewById("r1") } returns Result.Success(review)
        coEvery { reports.hasReported("r1") } returns Result.Success(alreadyReported)
        return ReviewDetailViewModel(reviews, reports, session).also { it.cargar("r1") }
    }

    @Test
    fun anotherUsersReview_canBeReported() {
        val state = loaded().uiState.value

        assertEquals("r1", state.review?.id)
        assertTrue(state.canReport)
        assertFalse(state.alreadyReported)
    }

    @Test
    fun myOwnReview_cannotBeReported_andIsNotQueried() {
        val vm = loaded(me = "bob")

        assertFalse(vm.uiState.value.canReport)
        coVerify(exactly = 0) { reports.hasReported(any()) }

        vm.onReportClick()
        assertFalse(vm.uiState.value.showReportDialog)
    }

    @Test
    fun withoutSession_cannotReport() {
        assertFalse(loaded(me = null).uiState.value.canReport)
    }

    @Test
    fun alreadyReported_isReflectedAndTheDialogDoesNotOpen() {
        val vm = loaded(alreadyReported = true)

        assertTrue(vm.uiState.value.alreadyReported)
        vm.onReportClick()
        assertFalse(vm.uiState.value.showReportDialog)
    }

    @Test
    fun reportClick_opensTheDialog_andDismissClosesIt() {
        val vm = loaded()

        vm.onReportClick()
        assertTrue(vm.uiState.value.showReportDialog)

        vm.onReportDismissed()
        assertFalse(vm.uiState.value.showReportDialog)
    }

    @Test
    fun sendingAReport_closesTheDialog_marksItReported_andShowsThanks() {
        val vm = loaded()
        coEvery { reports.report("r1", ReportReason.SPAM) } returns Result.Success(Unit)
        vm.onReportClick()

        vm.enviarReporte(ReportReason.SPAM)

        val state = vm.uiState.value
        assertFalse(state.showReportDialog)
        assertFalse(state.isReporting)
        assertTrue(state.alreadyReported)
        assertEquals(R.string.report_sent, state.infoMessageRes)
        coVerify(exactly = 1) { reports.report("r1", ReportReason.SPAM) }

        vm.onInfoMessageShown()
        assertNull(vm.uiState.value.infoMessageRes)
    }

    @Test
    fun aFailedReport_keepsTheDialogOpen_andShowsTheError() {
        val vm = loaded()
        coEvery { reports.report("r1", ReportReason.FAKE) } returns
            Result.Failure(IOException("sin red"), R.string.error_network)
        vm.onReportClick()

        vm.enviarReporte(ReportReason.FAKE)

        val state = vm.uiState.value
        assertTrue(state.showReportDialog)
        assertFalse(state.isReporting)
        assertFalse(state.alreadyReported)
        assertEquals(R.string.error_network, state.actionErrorRes)
    }
}

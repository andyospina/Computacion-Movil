package com.example.myapplication.viewmodels

import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.Result
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.LocationRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.ui.screens.NewReview.NewReviewViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Ubicación opcional al publicar una reseña (insumo del mapa de las últimas 24 h). */
class NewReviewLocationTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val products = mockk<ProductRepository>()
    private val reviews = mockk<ReviewRepository>()
    private val locations = mockk<LocationRepository>()
    private val session = mockk<CurrentUserIdProvider>()
    private val bogota = GeoLocation(4.711, -74.072)

    private fun viewModel(): NewReviewViewModel {
        every { session.currentUserId() } returns "u1"
        coEvery { reviews.createReview(any(), any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        return NewReviewViewModel(products, reviews, locations, session).apply {
            inicializar(productId = "p1", reviewId = "")
            updatePuntuacion(5)
            updateTitulo("Excelente")
            updateResena("Lo recomiendo")
        }
    }

    @Test
    fun byDefault_theLocationIsNotRequestedNorSent() {
        val vm = viewModel()

        vm.publicarButtonPress()

        coVerify(exactly = 0) { locations.getCurrentLocation() }
        coVerify { reviews.createReview("p1", "u1", 5, "Excelente", "Lo recomiendo", null) }
        assertTrue(vm.uiState.value.publicado)
    }

    @Test
    fun whenTheUserOptsIn_theReviewIsPublishedWithTheCurrentLocation() {
        val vm = viewModel()
        coEvery { locations.getCurrentLocation() } returns bogota
        vm.onPermisoUbicacionResultado(concedido = true)

        vm.publicarButtonPress()

        coVerify { reviews.createReview("p1", "u1", 5, "Excelente", "Lo recomiendo", bogota) }
    }

    @Test
    fun ifTheLocationCannotBeObtained_theReviewIsStillPublishedWithoutIt() {
        val vm = viewModel()
        coEvery { locations.getCurrentLocation() } returns null
        vm.onPermisoUbicacionResultado(concedido = true)

        vm.publicarButtonPress()

        coVerify { reviews.createReview("p1", "u1", 5, "Excelente", "Lo recomiendo", null) }
        assertTrue(vm.uiState.value.publicado)
    }

    @Test
    fun deniedPermission_keepsTheOptionOffAndFlagsIt() {
        val vm = viewModel()

        vm.onPermisoUbicacionResultado(concedido = false)

        assertFalse(vm.uiState.value.incluirUbicacion)
        assertTrue(vm.uiState.value.permisoUbicacionDenegado)

        vm.onIncluirUbicacionDesactivado()
        assertFalse(vm.uiState.value.permisoUbicacionDenegado)
    }
}

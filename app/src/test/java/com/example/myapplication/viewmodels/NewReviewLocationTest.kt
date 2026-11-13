package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.LocationDisabledException
import com.example.myapplication.data.LocationUnavailableException
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Ubicación al publicar una reseña: opt-in, permiso + GPS obligatorios y sin publicaciones «a medias». */
class NewReviewLocationTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val products = mockk<ProductRepository>()
    private val reviews = mockk<ReviewRepository>()
    private val locations = mockk<LocationRepository>()
    private val session = mockk<CurrentUserIdProvider>()
    private val bogota = GeoLocation(4.711, -74.072)

    private fun viewModel(gpsOn: Boolean = true): NewReviewViewModel {
        every { session.currentUserId() } returns "u1"
        every { locations.isLocationEnabled() } returns gpsOn
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
        coEvery { locations.getCurrentLocation() } returns Result.Success(bogota)
        vm.onPermisoUbicacionResultado(concedido = true)

        vm.publicarButtonPress()

        coVerify { reviews.createReview("p1", "u1", 5, "Excelente", "Lo recomiendo", bogota) }
        assertTrue(vm.uiState.value.publicado)
    }

    @Test
    fun ifTheLocationCannotBeObtained_theReviewIsNotPublished_andTheFormIsKept() {
        val vm = viewModel()
        coEvery { locations.getCurrentLocation() } returns
            Result.Failure(LocationUnavailableException(), R.string.error_location_unavailable)
        vm.onPermisoUbicacionResultado(concedido = true)

        vm.publicarButtonPress()

        coVerify(exactly = 0) { reviews.createReview(any(), any(), any(), any(), any(), any()) }
        assertFalse(vm.uiState.value.publicado)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(R.string.error_location_unavailable, vm.uiState.value.errorMessageRes)
        assertEquals("Excelente", vm.uiState.value.titulo)
        assertTrue("sigue pidiendo ubicación para poder reintentar", vm.uiState.value.incluirUbicacion)
    }

    @Test
    fun ifTheGpsIsTurnedOffBeforePublishing_theOptionTurnsOffAndOffersTheSettings() {
        val vm = viewModel()
        coEvery { locations.getCurrentLocation() } returns
            Result.Failure(LocationDisabledException(), R.string.error_location_disabled)
        vm.onPermisoUbicacionResultado(concedido = true)

        vm.publicarButtonPress()

        assertFalse(vm.uiState.value.publicado)
        assertTrue(vm.uiState.value.gpsDesactivado)
        assertFalse(vm.uiState.value.incluirUbicacion)
    }

    @Test
    fun permissionGrantedButGpsOff_doesNotActivateTheOption() {
        val vm = viewModel(gpsOn = false)

        vm.onPermisoUbicacionResultado(concedido = true)

        assertFalse(vm.uiState.value.incluirUbicacion)
        assertTrue(vm.uiState.value.gpsDesactivado)
        assertFalse(vm.uiState.value.permisoUbicacionDenegado)
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

package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.LocationDisabledException
import com.example.myapplication.data.LocationPermissionDeniedException
import com.example.myapplication.data.LocationUnavailableException
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.DeviceLocationDataSource
import com.example.myapplication.data.repository.LocationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationRepositoryTest {

    private val source = mockk<DeviceLocationDataSource>()
    private val repository = LocationRepository(source)

    private fun device(permission: Boolean = true, gps: Boolean = true, location: GeoLocation? = GeoLocation(4.7, -74.0)) {
        every { source.hasPermission() } returns permission
        every { source.isLocationEnabled() } returns gps
        coEvery { source.getCurrentLocation() } returns location
    }

    private fun failure(result: Result<GeoLocation>) = result as Result.Failure

    @Test
    fun withPermissionAndGps_returnsTheLocation() = runTest {
        device()

        assertEquals(GeoLocation(4.7, -74.0), (repository.getCurrentLocation() as Result.Success).data)
    }

    @Test
    fun withoutPermission_failsWithItsOwnMessage_andNeverAsksTheGps() = runTest {
        device(permission = false)

        val result = failure(repository.getCurrentLocation())

        assertTrue(result.exception is LocationPermissionDeniedException)
        assertEquals(R.string.error_location_permission, result.messageRes)
        coVerify(exactly = 0) { source.getCurrentLocation() }
    }

    @Test
    fun withGpsOff_failsSoTheScreenCanOfferTheSettings() = runTest {
        device(gps = false)

        val result = failure(repository.getCurrentLocation())

        assertTrue(result.exception is LocationDisabledException)
        assertEquals(R.string.error_location_disabled, result.messageRes)
    }

    @Test
    fun noFixAvailable_failsWithUnavailable() = runTest {
        device(location = null)

        val result = failure(repository.getCurrentLocation())

        assertTrue(result.exception is LocationUnavailableException)
        assertEquals(R.string.error_location_unavailable, result.messageRes)
    }

    @Test
    fun anInvalidFix_isTreatedAsUnavailable() = runTest {
        device(location = GeoLocation(200.0, 0.0))

        assertTrue(failure(repository.getCurrentLocation()).exception is LocationUnavailableException)
    }

    @Test
    fun aSecurityExceptionFromThePlatform_becomesThePermissionMessage() = runTest {
        device()
        coEvery { source.getCurrentLocation() } throws SecurityException("revocado")

        assertEquals(R.string.error_location_permission, failure(repository.getCurrentLocation()).messageRes)
    }
}

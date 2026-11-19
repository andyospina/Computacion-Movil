package com.example.myapplication.repository

import com.example.myapplication.data.ReportReason
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.ReportRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.NotAuthenticatedException
import com.example.myapplication.data.repository.ReportRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/** Reportes de reseñas con DataSource simulado. */
class ReportRepositoryTest {

    private val source = mockk<ReportRemoteDataSource>()
    private val session = mockk<CurrentUserIdProvider>()
    private val repository = ReportRepository(source, session)

    @Test
    fun report_savesTheReportForTheSessionUser() = runTest {
        every { session.currentUserId() } returns "u1"
        coEvery { source.hasReported("u1", "r1") } returns false
        coEvery { source.report("u1", "r1", ReportReason.SPAM) } returns Unit

        val result = repository.report("r1", ReportReason.SPAM)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { source.report("u1", "r1", ReportReason.SPAM) }
    }

    @Test
    fun report_whenAlreadyReported_isSuccessfulWithoutWritingAgain() = runTest {
        every { session.currentUserId() } returns "u1"
        coEvery { source.hasReported("u1", "r1") } returns true

        val result = repository.report("r1", ReportReason.FAKE)

        assertTrue(result is Result.Success)
        coVerify(exactly = 0) { source.report(any(), any(), any()) }
    }

    @Test
    fun report_withoutSession_failsWithNotAuthenticated() = runTest {
        every { session.currentUserId() } returns null

        val result = repository.report("r1", ReportReason.OTHER)

        assertTrue(result is Result.Failure)
        assertTrue((result as Result.Failure).exception is NotAuthenticatedException)
        coVerify(exactly = 0) { source.report(any(), any(), any()) }
    }

    @Test
    fun report_whenTheNetworkFails_returnsAFailure() = runTest {
        every { session.currentUserId() } returns "u1"
        coEvery { source.hasReported("u1", "r1") } returns false
        coEvery { source.report(any(), any(), any()) } throws IOException("sin red")

        val result = repository.report("r1", ReportReason.OFFENSIVE)

        assertTrue(result is Result.Failure)
    }

    @Test
    fun hasReported_withoutSession_isFalse() = runTest {
        every { session.currentUserId() } returns null

        assertEquals(Result.Success(false), repository.hasReported("r1"))
    }

    @Test
    fun hasReported_readsFromTheDataSource() = runTest {
        every { session.currentUserId() } returns "u1"
        coEvery { source.hasReported("u1", "r1") } returns true

        assertEquals(Result.Success(true), repository.hasReported("r1"))
    }

    @Test
    fun reportReason_roundTripsItsStoredValue() {
        ReportReason.entries.forEach { assertEquals(it, ReportReason.fromValue(it.value)) }
        assertEquals(null, ReportReason.fromValue("desconocido"))
    }
}

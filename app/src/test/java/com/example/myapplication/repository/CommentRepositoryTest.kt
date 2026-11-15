package com.example.myapplication.repository

import com.example.myapplication.R
import com.example.myapplication.data.Comment
import com.example.myapplication.data.Result
import com.example.myapplication.data.User
import com.example.myapplication.data.datasource.CommentRemoteDataSource
import com.example.myapplication.data.dto.FirestoreAuthorDto
import com.example.myapplication.data.dto.FirestoreCommentDto
import com.example.myapplication.data.dto.NewCommentDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.repository.CommentRepository
import com.example.myapplication.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/** [CommentRepository] con DataSource, perfil y sesión simulados. */
class CommentRepositoryTest {

    private val remote = mockk<CommentRemoteDataSource>()
    private val users = mockk<UserRepository>()
    private val session = mockk<CurrentUserIdProvider>()
    private val repository = CommentRepository(remote, users, session)

    private fun dto(id: String, userId: String, createdAt: Date? = Date(1_000)) = FirestoreCommentDto(
        id = id, reviewId = "r1", userId = userId, text = "Texto $id",
        user = FirestoreAuthorDto(name = "Nombre $userId", username = userId, profileImage = "https://img/$userId"),
        createdAt = createdAt
    )

    private fun me(uid: String?) = every { session.currentUserId() } returns uid

    private suspend fun observed(reviewAuthorId: String? = "autor"): List<Comment> =
        (repository.observeComments("r1", reviewAuthorId).first() as Result.Success).data

    @Test
    fun mapsTheDocumentToTheDomainModel() = runTest {
        me("ana")
        every { remote.observeComments("r1") } returns flowOf(listOf(dto("c1", "luis", Date(5_000))))

        val comment = observed().single()

        assertEquals("c1", comment.id)
        assertEquals("r1", comment.reviewId)
        assertEquals("Nombre luis", comment.authorName)
        assertEquals("https://img/luis", comment.authorImage)
        assertEquals("Texto c1", comment.text)
        assertEquals(5_000L, comment.createdAtMillis)
    }

    @Test
    fun onlyTheCommentAuthorCanDeleteIt_whenTheViewerIsNotTheReviewAuthor() = runTest {
        me("ana")
        every { remote.observeComments("r1") } returns flowOf(listOf(dto("mio", "ana"), dto("ajeno", "luis")))

        val (mine, theirs) = observed()

        assertTrue(mine.isMine && mine.canDelete)
        assertFalse(theirs.isMine || theirs.canDelete)
    }

    @Test
    fun theReviewAuthorCanModerateEveryCommentInTheirConversation() = runTest {
        me("autor")
        every { remote.observeComments("r1") } returns flowOf(listOf(dto("c1", "ana"), dto("c2", "luis")))

        assertTrue(observed(reviewAuthorId = "autor").all { it.canDelete && !it.isMine })
    }

    @Test
    fun withoutSession_nothingIsMineOrDeletable() = runTest {
        me(null)
        every { remote.observeComments("r1") } returns flowOf(listOf(dto("c1", "ana")))

        val comment = observed(reviewAuthorId = null).single()

        assertFalse(comment.isMine)
        assertFalse(comment.canDelete)
    }

    @Test
    fun aCommentWaitingForTheServerDate_hasNoTimestampYet() = runTest {
        me("ana")
        every { remote.observeComments("r1") } returns flowOf(listOf(dto("pendiente", "ana", createdAt = null)))

        assertNull(observed().single().createdAtMillis)
    }

    @Test
    fun aListenerError_becomesAFailureWithATranslatableMessage() = runTest {
        me("ana")
        every { remote.observeComments("r1") } returns flow {
            throw FirebaseFirestoreException("denegado", FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }

        val result = repository.observeComments("r1", "autor").first()

        assertEquals(R.string.error_permission_denied, (result as Result.Failure).messageRes)
    }

    @Test
    fun addComment_copiesTheAuthorProfileAndTrimsTheText() = runTest {
        me("ana")
        coEvery { users.getUserById("ana") } returns Result.Success(
            User(name = "Ana Gómez", initials = "AG", memberSinceYear = 2024, reviewCount = 0, usefulVotes = 0,
                averageRating = 0.0, profileImage = "https://img/ana", username = "anag", id = "ana")
        )
        val sent = slot<NewCommentDto>()
        coEvery { remote.addComment(capture(sent)) } returns Unit

        val result = repository.addComment("r1", "   ¿Qué tal la batería?  ")

        assertTrue(result is Result.Success)
        assertEquals(
            NewCommentDto("r1", "ana", "¿Qué tal la batería?", "Ana Gómez", "anag", "https://img/ana"),
            sent.captured
        )
    }

    @Test
    fun addComment_withoutSession_failsWithoutTouchingTheBackend() = runTest {
        me(null)

        val result = repository.addComment("r1", "hola")

        assertEquals(R.string.error_session_required, (result as Result.Failure).messageRes)
        coVerify(exactly = 0) { remote.addComment(any()) }
    }

    @Test
    fun addComment_whenTheProfileCannotBeRead_returnsThatFailure() = runTest {
        me("ana")
        val failure = Result.Failure(RuntimeException("sin red"), R.string.error_api_unreachable)
        coEvery { users.getUserById("ana") } returns failure

        assertEquals(failure, repository.addComment("r1", "hola"))
        coVerify(exactly = 0) { remote.addComment(any()) }
    }

    @Test
    fun deleteComment_deletesById_andMapsPermissionErrors() = runTest {
        me("ana")
        val comment = dto("c1", "luis").toComment("ana", "autor")
        coEvery { remote.deleteComment("c1") } throws
            FirebaseFirestoreException("denegado", FirebaseFirestoreException.Code.PERMISSION_DENIED)

        val result = repository.deleteComment(comment)

        coVerify { remote.deleteComment("c1") }
        assertEquals(R.string.error_permission_denied, (result as Result.Failure).messageRes)
    }
}

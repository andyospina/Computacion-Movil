package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.Comment
import com.example.myapplication.data.MAX_COMMENT_LENGTH
import com.example.myapplication.data.Result
import com.example.myapplication.data.Review
import com.example.myapplication.data.repository.CommentRepository
import com.example.myapplication.data.repository.ReviewRepository
import com.example.myapplication.ui.screens.Comments.CommentsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Conversación de una reseña: carga, envío con validación, borrado con confirmación y tiempo real. */
class CommentsViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val reviews = mockk<ReviewRepository>()
    private val comments = mockk<CommentRepository>()

    private val review = Review(
        id = "r1", productId = "p1", author = "Bob", verifiedBuyer = false, rating = 5, title = "Excelentes",
        body = "Cuerpo", date = "", usefulCount = 0, recommended = true, userId = "bob", productName = "Audífonos"
    )

    private fun comment(id: String, canDelete: Boolean = false) = Comment(
        id = id, reviewId = "r1", userId = "u-$id", authorName = "Autor $id", authorImage = null,
        text = "Texto $id", createdAtMillis = 1L, isMine = canDelete, canDelete = canDelete
    )

    private fun loaded(vararg list: Comment): CommentsViewModel {
        coEvery { reviews.getReviewById("r1") } returns Result.Success(review)
        every { comments.observeComments("r1", "bob") } returns flowOf(Result.Success(list.toList()))
        return CommentsViewModel(reviews, comments).also { it.cargar("r1") }
    }

    @Test
    fun loadsTheReviewAndListensToItsComments_passingTheReviewAuthor() {
        val vm = loaded(comment("a"), comment("b"))

        val state = vm.uiState.value
        assertEquals("r1", state.review?.id)
        assertEquals(listOf("a", "b"), state.comentarios.map { it.id })
        assertFalse(state.isLoading)
        assertNull(state.errorMessageRes)
        verify { comments.observeComments("r1", "bob") }
    }

    @Test
    fun aNewCommentFromSomeoneElse_appearsWithoutReloading() {
        coEvery { reviews.getReviewById("r1") } returns Result.Success(review)
        val live = MutableSharedFlow<Result<List<Comment>>>(replay = 1)
        live.tryEmit(Result.Success(listOf(comment("a"))))
        every { comments.observeComments("r1", "bob") } returns live
        val vm = CommentsViewModel(reviews, comments).also { it.cargar("r1") }

        live.tryEmit(Result.Success(listOf(comment("a"), comment("b"))))

        assertEquals(listOf("a", "b"), vm.uiState.value.comentarios.map { it.id })
    }

    @Test
    fun aReviewThatCannotBeLoaded_showsTheErrorAndRetryWorks() {
        coEvery { reviews.getReviewById("r1") } returnsMany listOf(
            Result.Failure(RuntimeException("sin red"), R.string.error_api_unreachable),
            Result.Success(review)
        )
        every { comments.observeComments("r1", "bob") } returns flowOf(Result.Success(emptyList()))
        val vm = CommentsViewModel(reviews, comments).also { it.cargar("r1") }
        assertEquals(R.string.error_api_unreachable, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.isLoading)

        vm.reintentar()

        assertNull(vm.uiState.value.errorMessageRes)
        assertEquals("r1", vm.uiState.value.review?.id)
    }

    @Test
    fun sendingABlankComment_showsAValidationErrorAndDoesNotCallTheRepository() {
        val vm = loaded()
        vm.onTextoChange("   ")

        vm.enviar()

        assertEquals(R.string.comments_error_empty, vm.uiState.value.textoErrorRes)
        coVerify(exactly = 0) { comments.addComment(any(), any()) }
    }

    @Test
    fun sendingATooLongComment_showsAValidationError() {
        val vm = loaded()
        vm.onTextoChange("a".repeat(MAX_COMMENT_LENGTH + 1))

        vm.enviar()

        assertEquals(R.string.comments_error_too_long, vm.uiState.value.textoErrorRes)
        coVerify(exactly = 0) { comments.addComment(any(), any()) }
    }

    @Test
    fun typingAgain_clearsTheValidationError() {
        val vm = loaded()
        vm.enviar()

        vm.onTextoChange("h")

        assertNull(vm.uiState.value.textoErrorRes)
    }

    @Test
    fun aValidComment_isSentTrimmed_andTheBoxIsCleared() {
        val vm = loaded()
        coEvery { comments.addComment("r1", "¿Cuánto dura la batería?") } returns Result.Success(Unit)
        vm.onTextoChange("  ¿Cuánto dura la batería?  ")

        vm.enviar()

        coVerify { comments.addComment("r1", "¿Cuánto dura la batería?") }
        assertEquals("", vm.uiState.value.texto)
        assertFalse(vm.uiState.value.isSending)
    }

    @Test
    fun whileSending_aSecondTapDoesNotDuplicateTheComment() {
        val vm = loaded()
        val pending = CompletableDeferred<Result<Unit>>()
        coEvery { comments.addComment(any(), any()) } coAnswers { pending.await() }
        vm.onTextoChange("hola")

        vm.enviar()
        assertTrue(vm.uiState.value.isSending)
        vm.enviar()
        pending.complete(Result.Success(Unit))

        coVerify(exactly = 1) { comments.addComment(any(), any()) }
        assertFalse(vm.uiState.value.isSending)
    }

    @Test
    fun aFailedSend_keepsTheTextAndShowsTheError() {
        val vm = loaded()
        coEvery { comments.addComment(any(), any()) } returns
            Result.Failure(RuntimeException("denegado"), R.string.error_permission_denied)
        vm.onTextoChange("hola")

        vm.enviar()

        assertEquals("hola", vm.uiState.value.texto)
        assertEquals(R.string.error_permission_denied, vm.uiState.value.actionErrorRes)
        assertFalse(vm.uiState.value.isSending)
    }

    @Test
    fun deletingAsksForConfirmationFirst_andCancelKeepsTheComment() {
        val mine = comment("mio", canDelete = true)
        val vm = loaded(mine)

        vm.onBorrarClick(mine)
        assertEquals(mine, vm.uiState.value.comentarioPorBorrar)

        vm.onBorrarCancelado()
        assertNull(vm.uiState.value.comentarioPorBorrar)
        coVerify(exactly = 0) { comments.deleteComment(any()) }
    }

    @Test
    fun confirmingDeletesTheComment() {
        val mine = comment("mio", canDelete = true)
        val vm = loaded(mine)
        coEvery { comments.deleteComment(mine) } returns Result.Success(Unit)

        vm.onBorrarClick(mine)
        vm.confirmarBorrado()

        coVerify { comments.deleteComment(mine) }
        assertNull(vm.uiState.value.comentarioPorBorrar)
        assertNull(vm.uiState.value.actionErrorRes)
    }

    @Test
    fun aCommentThatCannotBeDeleted_neverOpensTheDialog() {
        val theirs = comment("ajeno", canDelete = false)
        val vm = loaded(theirs)

        vm.onBorrarClick(theirs)

        assertNull(vm.uiState.value.comentarioPorBorrar)
    }

    @Test
    fun ifTheCommentDisappearsWhileConfirming_theDialogCloses() {
        coEvery { reviews.getReviewById("r1") } returns Result.Success(review)
        val mine = comment("mio", canDelete = true)
        val live = MutableSharedFlow<Result<List<Comment>>>(replay = 1)
        live.tryEmit(Result.Success(listOf(mine)))
        every { comments.observeComments("r1", "bob") } returns live
        val vm = CommentsViewModel(reviews, comments).also { it.cargar("r1") }
        vm.onBorrarClick(mine)

        // El autor de la reseña lo borró desde otro dispositivo
        live.tryEmit(Result.Success(emptyList()))

        assertNull(vm.uiState.value.comentarioPorBorrar)
    }
}

package com.example.myapplication.ui.screens.Comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.Comment
import com.example.myapplication.data.MAX_COMMENT_LENGTH
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.CommentRepository
import com.example.myapplication.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Conversación de una reseña: muestra la reseña arriba y sus comentarios en tiempo real (un comentario
 * nuevo o borrado por otra persona aparece sin recargar). Valida el texto antes de enviarlo.
 */
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentsState())
    val uiState = _uiState.asStateFlow()

    private var reviewId: String = ""
    private var comentariosJob: Job? = null

    fun cargar(id: String) {
        if (id == reviewId && _uiState.value.review != null) return
        reviewId = id
        cargarResena()
    }

    fun reintentar() = cargarResena()

    private fun cargarResena() {
        _uiState.update { it.copy(isLoading = it.review == null, errorMessageRes = null) }
        viewModelScope.launch {
            when (val result = reviewRepository.getReviewById(reviewId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(review = result.data) }
                    escucharComentarios(reviewAuthorId = result.data.userId)
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = result.messageRes)
                }
            }
        }
    }

    // El autor de la reseña se pasa para que pueda borrar (moderar) los comentarios de su conversación.
    private fun escucharComentarios(reviewAuthorId: String) {
        comentariosJob?.cancel()
        comentariosJob = viewModelScope.launch {
            commentRepository.observeComments(reviewId, reviewAuthorId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update { state ->
                        state.copy(
                            comentarios = result.data,
                            isLoading = false,
                            errorMessageRes = null,
                            // Si otra persona ya lo borró, el diálogo de confirmación no tiene sentido
                            comentarioPorBorrar = state.comentarioPorBorrar
                                ?.takeIf { pendiente -> result.data.any { it.id == pendiente.id } }
                        )
                    }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = result.messageRes)
                    }
                }
            }
        }
    }

    fun onTextoChange(texto: String) {
        _uiState.update { it.copy(texto = texto, textoErrorRes = null) }
    }

    fun enviar() {
        val state = _uiState.value
        if (state.isSending || state.review == null) return
        val texto = state.texto.trim()
        val error = when {
            texto.isEmpty() -> R.string.comments_error_empty
            texto.length > MAX_COMMENT_LENGTH -> R.string.comments_error_too_long
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(textoErrorRes = error) }
            return
        }

        _uiState.update { it.copy(isSending = true, textoErrorRes = null) }
        viewModelScope.launch {
            when (val result = commentRepository.addComment(reviewId, texto)) {
                is Result.Success -> _uiState.update { it.copy(isSending = false, texto = "") }
                // Se conserva el texto para que el usuario pueda reintentar sin volver a escribirlo
                is Result.Failure -> _uiState.update { it.copy(isSending = false, actionErrorRes = result.messageRes) }
            }
        }
    }

    fun toggleLike() {
        val review = _uiState.value.review ?: return
        viewModelScope.launch {
            when (val result = reviewRepository.toggleLike(review)) {
                is Result.Success -> when (val updated = reviewRepository.getReviewById(review.id)) {
                    is Result.Success -> _uiState.update { it.copy(review = updated.data) }
                    is Result.Failure -> _uiState.update { it.copy(actionErrorRes = updated.messageRes) }
                }
                is Result.Failure -> _uiState.update { it.copy(actionErrorRes = result.messageRes) }
            }
        }
    }

    fun onBorrarClick(comentario: Comment) {
        if (!comentario.canDelete) return
        _uiState.update { it.copy(comentarioPorBorrar = comentario) }
    }

    fun onBorrarCancelado() {
        _uiState.update { it.copy(comentarioPorBorrar = null) }
    }

    fun confirmarBorrado() {
        val comentario = _uiState.value.comentarioPorBorrar ?: return
        _uiState.update { it.copy(comentarioPorBorrar = null) }
        viewModelScope.launch {
            val result = commentRepository.deleteComment(comentario)
            if (result is Result.Failure) _uiState.update { it.copy(actionErrorRes = result.messageRes) }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }
}

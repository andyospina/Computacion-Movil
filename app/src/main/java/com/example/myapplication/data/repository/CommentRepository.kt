package com.example.myapplication.data.repository

import com.example.myapplication.R
import com.example.myapplication.data.Comment
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.CommentRemoteDataSource
import com.example.myapplication.data.dto.NewCommentDto
import com.example.myapplication.data.identity.CurrentUserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Conversación (comentarios) de una reseña. Las reglas de contenido las valida el ViewModel antes de llamar. */
class CommentRepository @Inject constructor(
    private val remoteDataSource: CommentRemoteDataSource,
    private val userRepository: UserRepository,
    private val currentUserId: CurrentUserIdProvider
) {

    /**
     * Comentarios de la reseña en tiempo real. [reviewAuthorId] permite marcar como borrables los
     * comentarios ajenos cuando quien mira es el autor de la reseña.
     */
    fun observeComments(reviewId: String, reviewAuthorId: String?): Flow<Result<List<Comment>>> =
        remoteDataSource.observeComments(reviewId)
            .map { list ->
                val uid = currentUserId.currentUserId()
                list.map { it.toComment(uid, reviewAuthorId) }
            }
            .asResult()

    /** Publica [text] (ya validado) a nombre del usuario de la sesión, con la copia de su perfil. */
    suspend fun addComment(reviewId: String, text: String): Result<Unit> {
        val uid = currentUserId.currentUserId()
            ?: return Result.Failure(NotAuthenticatedException(), R.string.error_session_required)
        val author = when (val result = userRepository.getUserById(uid)) {
            is Result.Success -> result.data
            is Result.Failure -> return result
        }
        return runCatchingApi {
            remoteDataSource.addComment(
                NewCommentDto(
                    reviewId = reviewId,
                    userId = uid,
                    text = text.trim(),
                    authorName = author.name,
                    authorUsername = author.username,
                    authorImage = author.profileImage
                )
            )
        }
    }

    suspend fun deleteComment(comment: Comment): Result<Unit> = runCatchingApi {
        if (currentUserId.currentUserId() == null) throw NotAuthenticatedException()
        remoteDataSource.deleteComment(comment.id)
    }
}

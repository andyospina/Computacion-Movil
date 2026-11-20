package com.example.myapplication.ui.screens.Comments

import androidx.annotation.StringRes
import com.example.myapplication.data.Comment
import com.example.myapplication.data.Review

data class CommentsState(
    val review: Review? = null,
    val comentarios: List<Comment> = emptyList(),
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    // Caja de texto para escribir un comentario nuevo
    val texto: String = "",
    @param:StringRes val textoErrorRes: Int? = null,
    val isSending: Boolean = false,
    // Comentario pendiente de confirmar en el diálogo «¿Borrar este comentario?»
    val comentarioPorBorrar: Comment? = null,
    @param:StringRes val actionErrorRes: Int? = null,
    // Reportar la reseña: solo se ofrece en reseñas ajenas y con sesión iniciada
    val canReport: Boolean = false,
    val alreadyReported: Boolean = false,
    val showReportDialog: Boolean = false,
    val isReporting: Boolean = false,
    @param:StringRes val infoMessageRes: Int? = null
)

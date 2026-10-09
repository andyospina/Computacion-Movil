package com.example.myapplication.ui.screens.NewReview

import androidx.annotation.StringRes

data class NewReviewState(
    val puntuacion: Int = 0,
    val titulo: String = "",
    val resena: String = "",
    val usuario: String = "",
    val correo: String = "",
    val publicado: Boolean = false,
    val productId: String = "",
    val reviewId: String? = null,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null
) {
    val esEdicion: Boolean get() = reviewId != null
}

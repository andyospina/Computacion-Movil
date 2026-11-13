package com.example.myapplication.ui.screens.Map

import androidx.annotation.StringRes
import com.example.myapplication.data.Review

data class ReviewsMapState(
    // Reseñas con ubicación de las últimas 24 horas, de la más reciente a la más antigua
    val reviews: List<Review> = emptyList(),
    val selectedReviewId: String? = null,
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null
) {
    /** La reseña del marcador tocado; null si no hay selección o ya salió de la ventana de 24 h. */
    val selected: Review? get() = reviews.firstOrNull { it.id == selectedReviewId }
}

package com.example.myapplication.ui.screens.LikedReviews

import androidx.annotation.StringRes
import com.example.myapplication.data.Review

data class LikedReviewsState(
    val resenas: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

package com.example.myapplication.ui.screens.Feed

import androidx.annotation.StringRes
import com.example.myapplication.data.Review

data class FeedState(
    val resenas: List<Review> = emptyList(),
    // Cuántas personas sigue el usuario: distingue «no sigues a nadie» de «no han publicado»
    val cantidadSeguidos: Int = 0,
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

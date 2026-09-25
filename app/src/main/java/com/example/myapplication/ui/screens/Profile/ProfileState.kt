package com.example.myapplication.ui.screens.Profile

import androidx.annotation.StringRes
import com.example.myapplication.data.Review
import com.example.myapplication.data.User

data class ProfileState(
    val user: User? = null,
    val misResenas: List<Review> = emptyList(),
    val email: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null,
    val loggedOut: Boolean = false
)

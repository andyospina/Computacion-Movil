package com.example.myapplication.ui.screens.Profile

import com.example.myapplication.data.Review
import com.example.myapplication.data.User

data class ProfileState(
    val user: User? = null,
    val misResenas: List<Review> = emptyList()
)

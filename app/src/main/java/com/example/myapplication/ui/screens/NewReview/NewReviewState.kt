package com.example.myapplication.ui.screens.NewReview

data class NewReviewState(
    val puntuacion: Int = 0,
    val titulo: String = "",
    val resena: String = "",
    val usuario: String = "",
    val correo: String = "",
    val publicado: Boolean = false
)

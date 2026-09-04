package com.example.myapplication.ui.screens.Reviews

import com.example.myapplication.data.Product
import com.example.myapplication.data.Review
import com.example.myapplication.ui.screens.Reviews.componentes.OrdenResenas

data class ReviewsListState(
    val productId: String = "",
    val producto: Product? = null,
    val todasLasResenas: List<Review> = emptyList(),
    val resenasFiltradas: List<Review> = emptyList(),
    val orden: OrdenResenas = OrdenResenas.RECIENTES,
    val conFotos: Boolean = false,
    val cantidadVisible: Int = 4
)

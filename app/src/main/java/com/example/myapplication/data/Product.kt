package com.example.myapplication.data

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val reviewCount: Int,
    val recommendPercent: Int,
    val ratingBreakdown: List<Pair<Int, Float>>
)

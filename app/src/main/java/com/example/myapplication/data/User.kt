package com.example.myapplication.data

data class User(
    val name: String,
    val initials: String,
    val memberSinceYear: Int,
    val reviewCount: Int,
    val usefulVotes: Int,
    val averageRating: Double
)

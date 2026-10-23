package com.example.myapplication.ui.screens.UserProfile

import androidx.annotation.StringRes
import com.example.myapplication.data.FollowStats
import com.example.myapplication.data.Review
import com.example.myapplication.data.User

data class UserProfileState(
    val userId: String = "",
    val user: User? = null,
    val resenas: List<Review> = emptyList(),
    val followStats: FollowStats = FollowStats(),
    // El usuario de la sesión ya sigue a este perfil
    val siguiendo: Boolean = false,
    // El perfil abierto es el propio: no se muestra «Seguir»
    val esMiPerfil: Boolean = false,
    val isFollowLoading: Boolean = false,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null,
    @param:StringRes val actionErrorRes: Int? = null
)

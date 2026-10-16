package com.example.myapplication.ui.screens.Profile

import androidx.annotation.StringRes
import com.example.myapplication.data.Review
import com.example.myapplication.data.User

data class ProfileState(
    val user: User? = null,
    val misResenas: List<Review> = emptyList(),
    val email: String = "",
    val profileImageUrl: String? = null,
    // Subida de la foto de perfil
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null,
    // Carga del perfil y las reseñas desde el backend
    val isLoadingData: Boolean = false,
    @param:StringRes val dataErrorRes: Int? = null,
    // Edición de los datos del perfil
    val isSavingProfile: Boolean = false,
    @param:StringRes val profileSaveErrorRes: Int? = null,
    val profileSaved: Boolean = false,
    val loggedOut: Boolean = false
)

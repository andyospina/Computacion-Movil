package com.example.myapplication.data.dto

import com.example.myapplication.data.User

/** Abstracción común de las respuestas de usuario de cada backend (Retrofit, Firestore). */
interface UserDto {
    fun toUser(): User
}

/** Datos adicionales del usuario al registrarse. La contraseña nunca viaja aquí. */
data class RegisterUserDto(
    val username: String,
    val name: String,
    val email: String,
    val country: String = "",
    val bio: String = ""
)

/** Campos del perfil que el usuario puede editar. El username y el correo no cambian. */
data class UpdateUserDto(
    val name: String,
    val country: String = "",
    val bio: String = ""
)

internal fun initialsOf(name: String): String =
    name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

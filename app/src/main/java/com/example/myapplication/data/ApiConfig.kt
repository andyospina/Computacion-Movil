package com.example.myapplication.data

object ApiConfig {

    /** Backend Express en la máquina host, visto desde el emulador. Debe terminar en "/". */
    const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * VALOR TEMPORAL: el uid de Firebase Authentication todavía no está vinculado a los IDs
     * numéricos de la tabla `users` de PostgreSQL, así que todas las operaciones del usuario
     * actual (crear reseñas, ver el perfil) usan este usuario sembrado por el backend.
     * Reemplazar cuando se guarde el uid de Firebase en una columna de `users`.
     */
    const val TEMP_CURRENT_USER_ID = "1"

    /** Con Retrofit no hay tiempo real: las pantallas "en vivo" vuelven a consultar cada 15 s. */
    const val POLL_INTERVAL_MS = 15_000L
}

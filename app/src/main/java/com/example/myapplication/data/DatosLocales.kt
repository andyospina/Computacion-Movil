package com.example.myapplication.data

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.model.EstadoResena
import com.example.myapplication.model.Notificacion
import com.example.myapplication.model.Perfil
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena

/**
 * Datos locales quemados (hardcodeados) usados como fuente de datos
 * mientras la app no tiene backend.
 */
object DatosLocales {

    val productos = listOf(
        Producto(1, "Grabadora de voz AI", "Tecnología", 4.8, 273, 88),
        Producto(2, "Sérum facial vitamina C", "Belleza", 4.2, 158, 0),
        Producto(3, "Cafetera espresso mini", "Hogar", 4.5, 96, 0),
        Producto(4, "Audífonos inalámbricos Pro", "Tecnología", 3.9, 412, 0),
        Producto(5, "Audífonos deportivos X2", "Tecnología", 4.4, 87, 0),
        Producto(6, "Audífonos con cancelación", "Tecnología", 4.7, 230, 0)
    )

    /** Producto destacado de la app — el que tiene reseñas reales. */
    val productoDestacado = productos.first { it.id == 1 }

    val perfilUsuario = Perfil(
        nombre = "Emily Morales",
        miembroDesde = 2024,
        totalResenas = 23,
        votosUtiles = 142,
        promedio = 4.3
    )

    val resenas = listOf(
        Resena(
            id = 1,
            productoId = 1,
            autor = "Emily",
            compradorVerificado = true,
            puntuacion = 5,
            titulo = "100000/10 recommend!!",
            comentario = "I hadn't even had my recorder a week and I had used " +
                "almost 300 minutes! With thousands of templates to choose " +
                "from, it's extremely versatile…",
            fecha = "Jul 13, 2026",
            votosUtiles = 2,
            recomendado = true
        ),
        Resena(
            id = 2,
            productoId = 1,
            autor = "Carlos",
            compradorVerificado = true,
            puntuacion = 5,
            titulo = "Batería increíble",
            comentario = "La batería dura toda la semana y la transcripción es " +
                "sorprendentemente precisa, incluso en reuniones largas.",
            fecha = "Ago 2, 2026",
            votosUtiles = 14,
            recomendado = true
        ),
        Resena(
            id = 3,
            productoId = 1,
            autor = "Lucía",
            compradorVerificado = true,
            puntuacion = 4,
            titulo = "Cumple lo que promete",
            comentario = "Muy buena, aunque la app tarda un poco en sincronizar. " +
                "Por lo demás, cumple todo lo que promete.",
            fecha = "Jul 28, 2026",
            votosUtiles = 9
        ),
        Resena(
            id = 4,
            productoId = 1,
            autor = "Diego",
            compradorVerificado = true,
            puntuacion = 3,
            titulo = "Correcta, sin más",
            comentario = "Hace su trabajo pero esperaba mejor micrófono en " +
                "exteriores. El soporte respondió rápido.",
            fecha = "Jul 20, 2026",
            votosUtiles = 3
        )
    )

    /** Reseñas que aparecen en el perfil de Emily, incluyendo un borrador sin publicar. */
    val misResenas = listOf(
        resenas[0].copy(productoId = 1),
        Resena(
            id = 101,
            productoId = 2,
            autor = "Emily Morales",
            compradorVerificado = true,
            puntuacion = 3,
            titulo = "Sérum facial vitamina C",
            comentario = "Bueno, pero el envase podría mejorar.",
            fecha = "Jun 30, 2026",
            votosUtiles = 5
        ),
        Resena(
            id = 102,
            productoId = 3,
            autor = "Emily Morales",
            compradorVerificado = true,
            puntuacion = 0,
            titulo = "Cafetera espresso mini",
            comentario = "Borrador — sin terminar",
            fecha = "Ago 10, 2026",
            votosUtiles = 0,
            estado = EstadoResena.BORRADOR
        )
    )

    val notificaciones = listOf(
        Notificacion(
            id = 1,
            icono = "★",
            mensaje = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("14 personas") }
                append(" marcaron tu reseña de ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Grabadora de voz AI") }
                append(" como útil")
            },
            tiempo = "Hace 2 horas",
            leida = false
        ),
        Notificacion(
            id = 2,
            icono = "✓",
            mensaje = buildAnnotatedString {
                append("Tu reseña de ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Sérum facial vitamina C") }
                append(" fue verificada y publicada")
            },
            tiempo = "Ayer",
            leida = false
        ),
        Notificacion(
            id = 3,
            icono = "💬",
            mensaje = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("La marca") }
                append(" respondió a tu reseña: “Gracias por tu comentario, Emily…”")
            },
            tiempo = "Hace 3 días",
            leida = true
        ),
        Notificacion(
            id = 4,
            icono = "✍️",
            mensaje = buildAnnotatedString {
                append("Compraste ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Cafetera espresso mini") }
                append(" hace 2 semanas · ¿cómo te fue?")
            },
            tiempo = "Hace 5 días",
            leida = true
        )
    )

    val busquedasRecientes = listOf("grabadora de voz", "sérum vitamina c", "cafetera")
}

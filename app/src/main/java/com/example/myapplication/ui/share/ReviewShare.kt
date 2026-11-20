package com.example.myapplication.ui.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import com.example.myapplication.data.Review

/** Máximo de caracteres del cuerpo de la reseña que viajan en el texto compartido. */
const val SHARE_BODY_MAX_LENGTH = 280

/** Estrellas llenas y vacías de una puntuación, p. ej. 4 → «★★★★☆». */
fun starsText(rating: Int, maxStars: Int = 5): String {
    val filled = rating.coerceIn(0, maxStars)
    return "★".repeat(filled) + "☆".repeat(maxStars - filled)
}

/** Recorta [text] a [max] caracteres añadiendo «…» (sin cortar si ya cabe). */
fun truncateForShare(text: String, max: Int = SHARE_BODY_MAX_LENGTH): String {
    val clean = text.trim()
    return if (clean.length <= max) clean else clean.take(max - 1).trimEnd() + "…"
}

/**
 * Texto que se comparte de una reseña. [format] es la plantilla traducible (R.string.share_review_text)
 * con los argumentos: 1 autor, 2 producto, 3 estrellas, 4 título, 5 cuerpo, 6 nombre de la app.
 * Si la reseña no trae nombre de producto se usa el título.
 */
fun buildReviewShareText(review: Review, format: String, appName: String): String =
    String.format(
        format,
        review.author.ifBlank { appName },
        review.productName.ifBlank { review.title },
        starsText(review.rating),
        review.title,
        truncateForShare(review.body),
        appName
    )

/** Asunto del mensaje (lo usan correo y algunas apps de mensajería). */
fun buildReviewShareSubject(review: Review, format: String): String =
    String.format(format, review.productName.ifBlank { review.title })

/** Abre el selector de apps de Android con el texto de la reseña. Devuelve false si no hay a dónde enviarlo. */
fun shareReview(context: Context, review: Review): Boolean {
    val text = buildReviewShareText(
        review = review,
        format = context.getString(R.string.share_review_text),
        appName = context.getString(R.string.app_name)
    )
    return launchShareChooser(
        context = context,
        subject = buildReviewShareSubject(review, context.getString(R.string.share_review_subject)),
        text = text,
        chooserTitle = context.getString(R.string.share_review_chooser_title)
    )
}

/** Abre el selector de apps con un texto plano; false si el dispositivo no tiene ninguna app que lo reciba. */
fun launchShareChooser(context: Context, subject: String, text: String, chooserTitle: String): Boolean {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        context.startActivity(chooser)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}

/** Acción de compartir lista para usar en un onClick: `val compartir = rememberCompartirResena()`. */
@Composable
fun rememberCompartirResena(): (Review) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { review ->
            if (!shareReview(context, review)) {
                android.widget.Toast.makeText(context, R.string.share_review_no_app, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.example.myapplication.ui.share

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import com.example.myapplication.data.Product

/**
 * Texto que se comparte de un producto. [format] es la plantilla traducible (R.string.share_product_text)
 * con los argumentos: 1 nombre, 2 estrellas, 3 promedio, 4 cantidad de reseñas, 5 % que lo recomienda,
 * 6 categoría y 7 nombre de la app.
 */
fun buildProductShareText(product: Product, format: String, appName: String): String =
    String.format(
        format,
        product.name,
        starsText(Math.round(product.rating).toInt()),
        product.rating.toString(),
        product.reviewCount,
        product.recommendPercent,
        product.category,
        appName
    )

/** Asunto del mensaje (lo usan correo y algunas apps de mensajería). */
fun buildProductShareSubject(product: Product, format: String): String =
    String.format(format, product.name)

/** Abre el selector de apps de Android con el producto. Devuelve false si no hay a dónde enviarlo. */
fun shareProduct(context: Context, product: Product): Boolean =
    launchShareChooser(
        context = context,
        subject = buildProductShareSubject(product, context.getString(R.string.share_product_subject)),
        text = buildProductShareText(
            product = product,
            format = context.getString(R.string.share_product_text),
            appName = context.getString(R.string.app_name)
        ),
        chooserTitle = context.getString(R.string.share_product_chooser_title)
    )

/** Acción de compartir un producto lista para un onClick: `val compartir = rememberCompartirProducto()`. */
@Composable
fun rememberCompartirProducto(): (Product) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { product ->
            if (!shareProduct(context, product)) {
                Toast.makeText(context, R.string.share_review_no_app, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

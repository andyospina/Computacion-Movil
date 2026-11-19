package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.BotonComentarios
import com.example.myapplication.ui.components.BotonCompartir
import com.example.myapplication.ui.components.BotonLike
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.share.rememberCompartirResena
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

/**
 * Tarjeta de una reseña. Con [onProductClick] muestra además el nombre del producto (feed
 * «Siguiendo»), con [onLikeClick] el corazón permite dar o quitar like y con [onCommentsClick] aparece
 * el acceso a la conversación de la reseña. Con [compartir] (activo por defecto) muestra el ícono que
 * abre el selector de apps de Android para compartir la reseña como texto.
 */
@Composable
fun TarjetaResena(
    resena: Review,
    modifier: Modifier = Modifier,
    onAuthorClick: (() -> Unit)? = null,
    onLikeClick: (() -> Unit)? = null,
    onProductClick: (() -> Unit)? = null,
    onCommentsClick: (() -> Unit)? = null,
    compartir: Boolean = true
) {
    val onShare = rememberCompartirResena()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reviewCard")
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        if (onProductClick != null && resena.productName.isNotBlank()) {
            Text(
                text = resena.productName,
                color = DeepLime,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onProductClick() }
                    .padding(bottom = 6.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // El nombre y la insignia ceden espacio (con «…») antes de empujar las estrellas fuera de la tarjeta
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resena.author,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = (if (onAuthorClick != null) Modifier.clickable { onAuthorClick() } else Modifier)
                        .weight(1f, fill = false)
                        .testTag("reviewAuthor")
                )

                if (resena.verifiedBuyer) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = DeepLime,
                        modifier = Modifier.height(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.reviews_verified_buyer_label),
                        color = DeepLime,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            StarRatingDisplay(rating = resena.rating, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = resena.title, fontWeight = FontWeight.Bold, modifier = Modifier.testTag("reviewTitle"))

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = resena.body, fontSize = 13.sp, color = GraySecondary)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // La fecha y «Recomendado» ocupan lo que sobra y se recortan; las acciones nunca se salen
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = resena.date,
                    fontSize = 12.sp,
                    color = GraySecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (resena.recommended) {
                    Text(
                        text = stringResource(R.string.reviews_recommended_label),
                        fontSize = 12.sp,
                        color = DeepLime,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }

            if (onCommentsClick != null) {
                BotonComentarios(onClick = onCommentsClick)
                Spacer(modifier = Modifier.width(4.dp))
            }

            if (compartir) {
                BotonCompartir(onClick = { onShare(resena) })
                Spacer(modifier = Modifier.width(4.dp))
            }

            BotonLike(likesCount = resena.likesCount, likedByMe = resena.likedByMe, onClick = onLikeClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaResenaPreview() {
    TarjetaResena(
        resena = LocalReviewProvider.reviews.first().copy(likesCount = 3, likedByMe = true),
        modifier = Modifier.padding(16.dp),
        onLikeClick = {},
        onProductClick = {}
    )
}

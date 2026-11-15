package com.example.myapplication.ui.screens.Comments.componentes

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.myapplication.data.Comment
import com.example.myapplication.ui.components.ProfileAsyncImage
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

/**
 * Un comentario de la conversación. Marca con «Autor» a quien escribió la reseña y muestra la papelera
 * solo si [Comment.canDelete].
 */
@Composable
fun TarjetaComentario(
    comentario: Comment,
    esAutorDeLaResena: Boolean,
    onAutorClick: () -> Unit,
    onBorrarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("commentItem"),
        verticalAlignment = Alignment.Top
    ) {
        ProfileAsyncImage(
            profileImage = comentario.authorImage,
            size = 34.dp,
            modifier = Modifier.clickable(onClick = onAutorClick)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .background(BorderColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nombre e insignia ocupan todo el ancho libre; un nombre largo se recorta con «…»
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (comentario.isMine) stringResource(R.string.comments_you) else comentario.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clickable(onClick = onAutorClick)
                    )
                    if (esAutorDeLaResena) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.comments_review_author_badge),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            modifier = Modifier
                                .background(DeepLime.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tiempoRelativo(comentario.createdAtMillis),
                    fontSize = 11.sp,
                    color = GraySecondary,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Text(
                text = comentario.text,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .testTag("commentText")
            )
        }

        if (comentario.canDelete) {
            IconButton(onClick = onBorrarClick, modifier = Modifier.size(36.dp).testTag("btnDeleteComment")) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.comments_delete),
                    tint = GraySecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * «hace 5 minutos». Menos de un minuto (o una hora del servidor un poco adelantada respecto al teléfono)
 * se muestra como «Justo ahora» en vez de «dentro de 0 minutos».
 */
@Composable
private fun tiempoRelativo(createdAtMillis: Long?): String {
    if (createdAtMillis == null) return stringResource(R.string.comments_sending)
    val now = System.currentTimeMillis()
    if (now - createdAtMillis < DateUtils.MINUTE_IN_MILLIS) return stringResource(R.string.comments_just_now)
    return DateUtils.getRelativeTimeSpanString(createdAtMillis, now, DateUtils.MINUTE_IN_MILLIS).toString()
}

@Preview(showBackground = true)
@Composable
fun TarjetaComentarioPreview() {
    TarjetaComentario(
        comentario = Comment(
            id = "c1", reviewId = "r1", userId = "u1", authorName = "Ada Lovelace", authorImage = null,
            text = "¿Cuánto te dura la batería con cancelación de ruido?", createdAtMillis = null,
            isMine = false, canDelete = true
        ),
        esAutorDeLaResena = true,
        onAutorClick = {},
        onBorrarClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

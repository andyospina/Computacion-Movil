package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.LikeRed

/**
 * Corazón con la cantidad de likes. Lleno si el usuario actual ya dio like.
 * Sin [onClick] solo muestra el contador (p. ej. en las reseñas propias).
 */
@Composable
fun BotonLike(
    likesCount: Int,
    likedByMe: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (likedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(
                if (likedByMe) R.string.content_description_unlike else R.string.content_description_like
            ),
            tint = if (likedByMe) LikeRed else GraySecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$likesCount",
            fontSize = 13.sp,
            fontWeight = if (likedByMe) FontWeight.Bold else FontWeight.Normal,
            color = if (likedByMe) LikeRed else GraySecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BotonLikePreview() {
    Row {
        BotonLike(likesCount = 3, likedByMe = false, onClick = {})
        BotonLike(likesCount = 4, likedByMe = true, onClick = {})
    }
}

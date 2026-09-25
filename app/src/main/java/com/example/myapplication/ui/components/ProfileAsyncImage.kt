package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

/**
 * Foto de perfil circular. Acepta una URL remota o una Uri local (content://) en forma de String.
 * - Sin imagen o si la carga falla, muestra el avatar por defecto.
 * - Mientras carga, muestra un indicador de progreso.
 */
@Composable
fun ProfileAsyncImage(
    profileImage: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val avatarPorDefecto = rememberVectorPainter(Icons.Filled.Person)
    var cargando by remember(profileImage) { mutableStateOf(profileImage != null) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ElectricLime),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(profileImage)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.content_description_profile_image),
            placeholder = avatarPorDefecto,
            error = avatarPorDefecto,
            fallback = avatarPorDefecto,
            contentScale = ContentScale.Crop,
            onLoading = { cargando = true },
            onSuccess = { cargando = false },
            onError = { cargando = false },
            modifier = Modifier.fillMaxSize()
        )

        if (cargando) {
            CircularProgressIndicator(
                color = Ink,
                strokeWidth = 2.dp,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileAsyncImagePreview() {
    ProfileAsyncImage(profileImage = null, size = 64.dp)
}

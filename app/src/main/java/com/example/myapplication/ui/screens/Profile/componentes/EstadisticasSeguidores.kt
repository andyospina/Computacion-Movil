package com.example.myapplication.ui.screens.Profile.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GraySecondary

/** «N Seguidores · M Siguiendo»; cada contador abre la lista correspondiente. Va sobre fondo oscuro. */
@Composable
fun EstadisticasSeguidores(
    followersCount: Int,
    followingCount: Int,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Contador(
            valor = followersCount,
            etiqueta = stringResource(R.string.profile_followers),
            onClick = onFollowersClick
        )
        Contador(
            valor = followingCount,
            etiqueta = stringResource(R.string.profile_following),
            onClick = onFollowingClick
        )
    }
}

@Composable
private fun Contador(valor: Int, etiqueta: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$valor ", color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = etiqueta, color = GraySecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101012)
@Composable
fun EstadisticasSeguidoresPreview() {
    EstadisticasSeguidores(
        followersCount = 12,
        followingCount = 4,
        onFollowersClick = {},
        onFollowingClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

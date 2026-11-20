package com.example.myapplication.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GraySecondary

/** Ícono para compartir una reseña con otras apps (junto al corazón y al acceso a comentarios). */
@Composable
fun BotonCompartir(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = GraySecondary,
    @StringRes descripcion: Int = R.string.share_review_action
) {
    Icon(
        imageVector = Icons.Outlined.Share,
        contentDescription = stringResource(descripcion),
        tint = tint,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("btnShare")
            .padding(4.dp)
            .size(18.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun BotonCompartirPreview() {
    BotonCompartir(onClick = {})
}

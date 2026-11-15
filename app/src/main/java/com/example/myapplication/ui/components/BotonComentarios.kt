package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GraySecondary

/** Acceso a la conversación (comentarios) de una reseña, al lado del corazón de likes. */
@Composable
fun BotonComentarios(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("btnComments")
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Comment,
            contentDescription = null,
            tint = GraySecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = stringResource(R.string.comments_action), fontSize = 13.sp, color = GraySecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun BotonComentariosPreview() {
    BotonComentarios(onClick = {})
}

package com.example.myapplication.ui.screens.Comments.componentes

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.myapplication.data.MAX_COMMENT_LENGTH
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.GraySecondary
import com.example.myapplication.ui.theme.Ink

/** Caja para escribir un comentario, con contador de caracteres y botón de enviar. */
@Composable
fun CampoComentario(
    texto: String,
    @StringRes errorRes: Int?,
    isSending: Boolean,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val excedido = texto.trim().length > MAX_COMMENT_LENGTH

    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = onTextoChange,
                placeholder = { Text(stringResource(R.string.comments_input_placeholder)) },
                isError = errorRes != null || excedido,
                enabled = !isSending,
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("commentInput")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onEnviarClick,
                enabled = !isSending,
                modifier = Modifier
                    .size(44.dp)
                    .background(ElectricLime, CircleShape)
                    .testTag("btnSendComment")
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Ink, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.comments_send),
                        tint = Ink
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
            if (errorRes != null) {
                Text(
                    text = stringResource(errorRes, MAX_COMMENT_LENGTH),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.comments_counter, texto.trim().length, MAX_COMMENT_LENGTH),
                color = if (excedido) MaterialTheme.colorScheme.error else GraySecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampoComentarioPreview() {
    CampoComentario(
        texto = "¡Gracias por la reseña!",
        errorRes = null,
        isSending = false,
        onTextoChange = {},
        onEnviarClick = {}
    )
}

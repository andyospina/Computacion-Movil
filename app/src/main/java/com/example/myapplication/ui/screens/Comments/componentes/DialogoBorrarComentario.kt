package com.example.myapplication.ui.screens.Comments.componentes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R

/** Confirmación antes de borrar un comentario (la acción no se puede deshacer). */
@Composable
fun DialogoBorrarComentario(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.comments_delete_confirm_title)) },
        text = { Text(stringResource(R.string.comments_delete_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirmar, modifier = Modifier.testTag("btnConfirmDeleteComment")) {
                Text(
                    text = stringResource(R.string.comments_delete_confirm_action),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(stringResource(R.string.comments_cancel)) }
        }
    )
}

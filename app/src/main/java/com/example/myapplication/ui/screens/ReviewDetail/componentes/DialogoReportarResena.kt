package com.example.myapplication.ui.screens.ReviewDetail.componentes

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.ReportReason
import com.example.myapplication.ui.theme.GraySecondary

@StringRes
fun ReportReason.labelRes(): Int = when (this) {
    ReportReason.SPAM -> R.string.report_reason_spam
    ReportReason.OFFENSIVE -> R.string.report_reason_offensive
    ReportReason.FAKE -> R.string.report_reason_fake
    ReportReason.OTHER -> R.string.report_reason_other
}

/** Pide el motivo del reporte. «Enviar» se habilita al elegir uno y se bloquea mientras se envía. */
@Composable
fun DialogoReportarResena(
    isSending: Boolean,
    onEnviar: (ReportReason) -> Unit,
    onCancelar: () -> Unit
) {
    var motivo by rememberSaveable { mutableStateOf<ReportReason?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(text = stringResource(R.string.report_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.report_dialog_message),
                    color = GraySecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(modifier = Modifier.selectableGroup()) {
                    ReportReason.entries.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSending, role = Role.RadioButton) { motivo = opcion }
                                .testTag("reason_${opcion.value}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = motivo == opcion, onClick = null, enabled = !isSending)
                            Text(
                                text = stringResource(opcion.labelRes()),
                                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { motivo?.let(onEnviar) },
                enabled = motivo != null && !isSending,
                modifier = Modifier.testTag("btnSendReport")
            ) {
                Text(text = stringResource(if (isSending) R.string.report_sending else R.string.report_submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, enabled = !isSending) {
                Text(text = stringResource(R.string.action_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DialogoReportarResenaPreview() {
    DialogoReportarResena(isSending = false, onEnviar = {}, onCancelar = {})
}

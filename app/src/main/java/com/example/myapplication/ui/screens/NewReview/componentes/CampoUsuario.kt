package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun CampoUsuario(
    modifier: Modifier = Modifier,
    usuario: String,
    onUsuarioChange: (String) -> Unit
) {

    OutlinedTextField(
        value = usuario,
        onValueChange = onUsuarioChange,
        label = {
            Text(stringResource(R.string.new_review_label_user_required))
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "CampoUsuario - Preview")
@Composable
fun CampoUsuarioPreview() {
    CampoUsuario(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        usuario = "Camila R.",
        onUsuarioChange = {}
    )
}
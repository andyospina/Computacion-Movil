package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun CampoResena(
    modifier: Modifier = Modifier,
    resena: String,
    onResenaChange: (String) -> Unit
) {

    OutlinedTextField(
        value = resena,
        onValueChange = onResenaChange,
        label = {
            Text(stringResource(R.string.new_review_label_review))
        },
        modifier = modifier.height(150.dp)
    )
}

@Preview(showBackground = true, name = "CampoResena - Preview")
@Composable
fun CampoResenaPreview() {
    CampoResena(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        resena = "Excelente producto, superó mis expectativas.",
        onResenaChange = {}
    )
}
package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CampoTitulo(
    titulo: String,
    onTituloChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = titulo,
        onValueChange = onTituloChange,
        placeholder = { Text("Título de tu reseña") },
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, name = "CampoTitulo - Preview")
@Composable
fun CampoTituloPreview() {
    MyApplicationTheme(darkTheme = false) {
        CampoTitulo(
            titulo = "100000/10 recommend!!",
            onTituloChange = {}
        )
    }
}

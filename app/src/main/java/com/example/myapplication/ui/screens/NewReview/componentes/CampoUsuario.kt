package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.RequeridoRojo

@Composable
fun CampoUsuario(
    usuario: String,
    onUsuarioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            Text(text = "*", fontSize = 11.sp, color = RequeridoRojo)
            Text(text = "Usuario:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedTextField(
            value = usuario,
            onValueChange = onUsuarioChange,
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "CampoUsuario - Preview")
@Composable
fun CampoUsuarioPreview() {
    MyApplicationTheme(darkTheme = false) {
        CampoUsuario(usuario = "Camila R.", onUsuarioChange = {})
    }
}

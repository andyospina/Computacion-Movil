package com.example.myapplication.ui.screens.PublishedReview.componentes


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BotonVolver(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD5FF00)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {

        Text(
            text = "Volver al inicio",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "BotonVolver - Preview")
@Composable
fun BotonVolverPreview() {
    BotonVolver(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    )
}
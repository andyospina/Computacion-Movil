package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
@Composable
fun ResumenNumerico(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "4.5 ★", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "273 Reseñas", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "88%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "Recomiendan", fontSize = 12.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true, name = "ResumenNumerico - Preview")
@Composable
fun ResumenNumericoPreview() {
    com.example.myapplication.ui.screens.Reviews.componentes.ResumenNumerico(
        modifier = Modifier.padding(16.dp)
    )
}
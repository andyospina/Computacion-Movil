package com.example.myapplication.ui.screens.ProductDetail.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun FotoProducto(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(BorderColor.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Foto del producto", color = GraySecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun FotoProductoPreview() {
    FotoProducto()
}

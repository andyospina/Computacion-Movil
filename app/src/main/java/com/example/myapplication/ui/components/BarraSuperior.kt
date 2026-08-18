package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarraSuperior(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color(0xFFD5FF00))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "☰",
            fontSize = 28.sp,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = "ReviewLab",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Login",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
@Composable
@Preview
fun BarraSuperiorPreview(){
    BarraSuperior()
}

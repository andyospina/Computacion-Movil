package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BarraEstrellas(
    estrellas: Int,
    porcentaje: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = estrellas.toString(),
            modifier = Modifier.width(20.dp),
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje)
                    .height(8.dp)
                    .background(
                        color = Color(0xFFD5FF00),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}
@Composable
@Preview(showBackground = true)
fun BarraEstrellasPreview(){
    BarraEstrellas(5,0.88f)
}
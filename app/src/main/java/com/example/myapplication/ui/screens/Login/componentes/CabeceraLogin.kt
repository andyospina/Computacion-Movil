package com.example.myapplication.ui.screens.Login.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Gray
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Paper
import com.example.myapplication.ui.theme.Sora

/**
 * Cabecera fija y siempre oscura (Ink), independiente del modo claro/oscuro
 * de la app — es un momento de marca, igual que en el diseño original.
 */
@Composable
fun CabeceraLogin(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Ink)
            .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoReviewLab()

        Text(
            text = "ReviewLab",
            modifier = Modifier.padding(top = 14.dp),
            fontFamily = Sora,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Paper
        )

        Text(
            text = "Opiniones reales de compradores reales",
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 12.sp,
            color = Gray
        )
    }
}

/** Logo en forma de burbuja de diálogo con la letra "R", como en el diseño. */
@Composable
private fun LogoReviewLab() {
    val color = ElectricLime

    Box(
        modifier = Modifier.size(64.dp)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(64.dp)) {
            val cola = Path().apply {
                moveTo(0f, 52.dp.toPx())
                lineTo(14.dp.toPx(), 52.dp.toPx())
                lineTo(0f, 64.dp.toPx())
                close()
            }
            drawPath(path = cola, color = color, style = Fill)
        }

        Box(
            modifier = Modifier
                .size(width = 58.dp, height = 52.dp)
                .background(color, RoundedCornerShape(15.dp, 15.dp, 15.dp, 0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                color = Ink
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CabeceraLoginPreview() {
    MyApplicationTheme(darkTheme = true) {
        CabeceraLogin()
    }
}

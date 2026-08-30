package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.Paper

/**
 * Marca "R" de ReviewLab: burbuja redondeada (esquina inferior izquierda recta)
 * más una cola triangular, replicando el diseño de referencia (reviewlab-logo-1b.html).
 */
private const val MARK_SIZE = 84f
private const val BUBBLE_WIDTH = 76f
private const val BUBBLE_HEIGHT = 68f
private const val BUBBLE_RADIUS = 20f
private const val TAIL_TOP = 66f
private const val TAIL_WIDTH = 18f
private const val TAIL_HEIGHT = 16f

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
    bubbleColor: Color = ElectricLime,
    markColor: Color = Ink
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val scale = size.toPx() / MARK_SIZE
            drawLogoMark(scale = scale, color = bubbleColor)
        }

        Box(
            modifier = Modifier.size(
                size * (BUBBLE_WIDTH / MARK_SIZE),
                size * (BUBBLE_HEIGHT / MARK_SIZE)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                color = markColor,
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.42f).sp
            )
        }
    }
}

private fun DrawScope.drawLogoMark(scale: Float, color: Color) {
    val bubbleWidth = BUBBLE_WIDTH * scale
    val bubbleHeight = BUBBLE_HEIGHT * scale
    val radius = CornerRadius(BUBBLE_RADIUS * scale)

    val bubblePath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, bubbleWidth, bubbleHeight),
                topLeft = radius,
                topRight = radius,
                bottomRight = radius,
                bottomLeft = CornerRadius.Zero
            )
        )
    }
    drawPath(bubblePath, color = color)

    val tailTop = TAIL_TOP * scale
    val tailWidth = TAIL_WIDTH * scale
    val tailHeight = TAIL_HEIGHT * scale
    val tailPath = Path().apply {
        moveTo(0f, tailTop)
        lineTo(tailWidth, tailTop)
        lineTo(0f, tailTop + tailHeight)
        close()
    }
    drawPath(tailPath, color = color)
}

/**
 * Logo completo con el wordmark "ReviewLab" en un fondo oscuro.
 */
@Composable
fun AppLogoWithWordmark(
    modifier: Modifier = Modifier,
    markSize: Dp = 56.dp,
    wordmarkColor: Color = Paper
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AppLogo(size = markSize)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "ReviewLab",
            color = wordmarkColor,
            fontWeight = FontWeight.Black,
            fontSize = (markSize.value * 0.42f).sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppLogoPreview() {
    AppLogo(size = 96.dp)
}

@Preview(showBackground = true, backgroundColor = 0xFF101012)
@Composable
fun AppLogoWithWordmarkPreview() {
    AppLogoWithWordmark(markSize = 64.dp)
}

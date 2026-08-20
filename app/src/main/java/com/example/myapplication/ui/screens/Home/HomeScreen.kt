package com.example.myapplication.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Pantalla de inicio para un usuario invitado (no autenticado). Corresponde
 * a "01 Empty state" del diseño: título, descripción, botón para escribir
 * una reseña (que primero exige iniciar sesión) y un FAB de carrito.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEscribirResena: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Calificaciones y Reseñas",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Comparte tus pensamientos con otras personas",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Escribe una Reseña",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                    .clickable { onEscribirResena() }
                    .padding(11.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(48.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { onEscribirResena() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛒", fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Preview")
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        HomeScreen(onEscribirResena = {})
    }
}

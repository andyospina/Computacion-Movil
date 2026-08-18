package com.example.myapplication.ui.screens.Reviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.screens.Reviews.componentes.BarraEstrellas
import com.example.myapplication.ui.screens.Reviews.componentes.ResenaEjemplo
import com.example.myapplication.ui.screens.Reviews.componentes.ResumenReviews
import com.example.myapplication.ui.screens.Reviews.componentes.EncabezadoReviews
import com.example.myapplication.ui.screens.Reviews.componentes.ResumenNumerico
import com.example.myapplication.ui.screens.Reviews.componentes.TituloProductoReviews
import com.example.myapplication.ui.screens.Reviews.componentes.ContenidoReviews

@Composable
fun ReviewsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            BarraSuperior(
                modifier = Modifier.fillMaxWidth()
            )

            ContenidoReviews(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "ReviewsScreen - Preview")
@Composable
fun ReviewsScreenPreview() {
    ReviewsScreen()
}
@Composable
fun BarrasCalificacion(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        BarraEstrellas(estrellas = 5, porcentaje = 0.88f, modifier = Modifier.fillMaxWidth())
        BarraEstrellas(estrellas = 4, porcentaje = 0.55f, modifier = Modifier.fillMaxWidth())
        BarraEstrellas(estrellas = 3, porcentaje = 0.40f, modifier = Modifier.fillMaxWidth())
        BarraEstrellas(estrellas = 2, porcentaje = 0.28f, modifier = Modifier.fillMaxWidth())
        BarraEstrellas(estrellas = 1, porcentaje = 0.12f, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true, name = "BarrasCalificacion - Preview")
@Composable
fun BarrasCalificacionPreview() {
    BarrasCalificacion(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
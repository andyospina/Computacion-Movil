package com.example.myapplication.ui.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.screens.Home.componentes.BotonEscribirResena
import com.example.myapplication.ui.screens.Home.componentes.DescripcionInicio
import com.example.myapplication.ui.screens.Home.componentes.TituloInicio

@Composable
fun HomeScreen(
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

            BarraSuperior()

            ContenidoInicio(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Preview")
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun ContenidoInicio(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        TituloInicio()

        Spacer(modifier = Modifier.height(12.dp))

        DescripcionInicio()

        Spacer(modifier = Modifier.height(40.dp))

        BotonEscribirResena(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "ContenidoInicio - Preview")
@Composable
fun ContenidoInicioPreview() {
    ContenidoInicio(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
}



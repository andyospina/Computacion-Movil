package com.example.myapplication.ui.screens.WriteReview.componentes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun TituloInicio(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.calificaciones_y_rese_as),
        modifier = modifier,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true, name = "TituloInicio - Preview")
@Composable
fun TituloInicioPreview() {
    TituloInicio(
        modifier = Modifier.padding(16.dp)
    )
}
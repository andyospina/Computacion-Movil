package com.example.myapplication.ui.screens.WriteReview.componentes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

    @SuppressLint("NotConstructor")
    @Composable
    fun DescripcionInicio(modifier: Modifier = Modifier) {
        Text(
            text = stringResource(R.string.comparte_tus_pensamientos_con_otras_personas),
            modifier = modifier,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }

    @Preview(showBackground = true, name = "DescripcionInicio - Preview")
    @Composable
    fun DescripcionInicioPreview() {
        DescripcionInicio(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
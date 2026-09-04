package com.example.myapplication.ui.screens.ReviewPublished.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun BotonesResumenPublicado(
    modifier: Modifier = Modifier,
    onVerMiResenaClick: () -> Unit,
    onVolverInicioClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onVerMiResenaClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = Ink),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(text = stringResource(R.string.review_published_view_my_review), fontWeight = FontWeight.Bold)
        }

        Text(
            text = stringResource(R.string.review_published_back_home),
            color = DeepLime,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onVolverInicioClick() }
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BotonesResumenPublicadoPreview() {
    BotonesResumenPublicado(
        onVerMiResenaClick = {},
        onVolverInicioClick = {}
    )
}

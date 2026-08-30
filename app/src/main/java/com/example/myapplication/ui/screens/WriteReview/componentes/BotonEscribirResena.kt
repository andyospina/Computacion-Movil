package com.example.myapplication.ui.screens.WriteReview.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

@Composable
fun BotonEscribirResena(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(55.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricLime
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = stringResource(R.string.escribe_una_rese_a),
            color = Ink,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "BotonEscribirResena - Preview")
@Composable
fun BotonEscribirResenaPreview() {
    BotonEscribirResena(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    )
}

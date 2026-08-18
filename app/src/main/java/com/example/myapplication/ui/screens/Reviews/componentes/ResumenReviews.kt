package com.example.myapplication.ui.screens.Reviews.componentes
import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.screens.Reviews.BarrasCalificacion

@Composable
fun ResumenReviews(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BarrasCalificacion(
            modifier = Modifier.weight(1f)
        )

        ResumenNumerico()
    }
}

@Preview(showBackground = true, name = "ResumenReviews - Preview")
@Composable
fun ResumenReviewsPreview() {
    com.example.myapplication.ui.screens.Reviews.componentes.ResumenReviews(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
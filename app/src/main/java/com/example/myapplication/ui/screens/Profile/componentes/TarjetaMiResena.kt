package com.example.myapplication.ui.screens.Profile.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.Review
import com.example.myapplication.data.ReviewStatus
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun TarjetaMiResena(
    resena: Review,
    nombreProducto: String,
    modifier: Modifier = Modifier
) {
    val esBorrador = resena.status == ReviewStatus.DRAFT

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = nombreProducto, fontWeight = FontWeight.Bold)
            StarRatingDisplay(rating = resena.rating, fontSize = 14.sp)
        }

        if (!esBorrador) {
            Text(text = resena.title, fontSize = 13.sp)
        } else {
            Text(text = resena.title, fontSize = 13.sp, color = GraySecondary)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = resena.date, fontSize = 12.sp, color = GraySecondary)
            Text(
                text = stringResource(if (esBorrador) R.string.status_draft else R.string.status_published),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (esBorrador) GraySecondary else DeepLime
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaMiResenaPreview() {
    TarjetaMiResena(
        resena = LocalReviewProvider.reviews.first(),
        nombreProducto = "Grabadora de voz AI",
        modifier = Modifier.padding(16.dp)
    )
}

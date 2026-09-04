package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun TarjetaResena(
    resena: Review,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = resena.author, fontWeight = FontWeight.Bold)

            if (resena.verifiedBuyer) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = DeepLime,
                    modifier = Modifier.height(14.dp)
                )
                Text(text = stringResource(R.string.reviews_verified_buyer_label), color = DeepLime, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            StarRatingDisplay(rating = resena.rating, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = resena.title, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = resena.body, fontSize = 13.sp, color = GraySecondary)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(text = resena.date, fontSize = 12.sp, color = GraySecondary)
            Text(
                text = stringResource(R.string.reviews_useful_count, resena.usefulCount),
                fontSize = 12.sp,
                color = GraySecondary
            )
            if (resena.recommended) {
                Text(
                    text = stringResource(R.string.reviews_recommended_label),
                    fontSize = 12.sp,
                    color = DeepLime,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaResenaPreview() {
    TarjetaResena(
        resena = LocalReviewProvider.reviews.first(),
        modifier = Modifier.padding(16.dp)
    )
}

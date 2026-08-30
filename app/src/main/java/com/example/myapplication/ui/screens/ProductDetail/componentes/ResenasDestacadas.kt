package com.example.myapplication.ui.screens.ProductDetail.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.LocalReviewProvider
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun ResenasDestacadas(
    resenas: List<Review>,
    onVerTodasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Reseñas destacadas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Ver todas >",
                color = DeepLime,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onVerTodasClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        resenas.forEach { resena ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = resena.author, fontWeight = FontWeight.Bold)
                    StarRatingDisplay(rating = resena.rating, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = resena.body, color = GraySecondary, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResenasDestacadasPreview() {
    ResenasDestacadas(
        resenas = LocalReviewProvider.featuredForProduct("grabadora-voz-ai"),
        onVerTodasClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

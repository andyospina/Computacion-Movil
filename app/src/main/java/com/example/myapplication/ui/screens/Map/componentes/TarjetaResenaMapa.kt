package com.example.myapplication.ui.screens.Map.componentes

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.Review
import com.example.myapplication.ui.components.StarRatingDisplay
import com.example.myapplication.ui.theme.GraySecondary

private const val MAX_BODY_LINES = 3

/** Información de la reseña del marcador tocado; se muestra sobre el mapa. */
@Composable
fun TarjetaResenaMapa(
    review: Review,
    onProductClick: () -> Unit,
    onDetailClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    StarRatingDisplay(rating = review.rating)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.content_description_close))
                }
            }

            Text(text = review.title, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = review.body, maxLines = MAX_BODY_LINES, overflow = TextOverflow.Ellipsis)

            val elapsed = review.createdAtMillis?.let {
                DateUtils.getRelativeTimeSpanString(it, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
            } ?: stringResource(R.string.map_just_now)
            Text(
                text = stringResource(R.string.map_review_meta, review.author, elapsed, review.likesCount),
                style = MaterialTheme.typography.bodySmall,
                color = GraySecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                Button(onClick = onDetailClick, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.map_view_detail))
                }
                OutlinedButton(onClick = onProductClick, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.map_view_product))
                }
            }
        }
    }
}

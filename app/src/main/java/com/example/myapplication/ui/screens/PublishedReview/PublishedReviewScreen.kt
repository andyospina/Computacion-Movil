package com.example.myapplication.ui.screens.PublishedReview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.screens.PublishedReview.componentes.IconoConfirmacion
import com.example.myapplication.ui.screens.PublishedReview.componentes.TarjetaResenaPublicada
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Sora

@Composable
fun PublishedReviewScreen(
    modifier: Modifier = Modifier,
    resena: Resena,
    producto: Producto,
    onVerMiResena: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconoConfirmacion()

        Text(
            text = "¡Reseña publicada!",
            modifier = Modifier.padding(top = 18.dp),
            fontFamily = Sora,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Text(
            text = "Gracias por compartir tu experiencia. Tu reseña ayudará " +
                "a otros compradores a decidir mejor.",
            modifier = Modifier.padding(top = 10.dp),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        TarjetaResenaPublicada(
            resena = resena,
            producto = producto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
        )

        Button(
            onClick = onVerMiResena,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(text = "Ver mi reseña", fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Volver al inicio",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onVolver() },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true, name = "PublishedReviewScreen - Preview")
@Composable
fun PublishedReviewScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        PublishedReviewScreen(
            resena = DatosLocales.resenas.first(),
            producto = DatosLocales.productoDestacado,
            onVerMiResena = {},
            onVolver = {}
        )
    }
}

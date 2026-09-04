package com.example.myapplication.ui.screens.Profile.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.data.User
import com.example.myapplication.ui.theme.DeepLime
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun EstadisticasPerfil(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
    ) {
        Estadistica(valor = "${user.reviewCount}", etiqueta = stringResource(R.string.profile_stat_reviews))
        Estadistica(valor = "${user.usefulVotes}", etiqueta = stringResource(R.string.profile_stat_useful_votes))
        Estadistica(valor = "${user.averageRating}★", etiqueta = stringResource(R.string.profile_stat_average), color = DeepLime)
    }
}

@Composable
private fun Estadistica(
    valor: String,
    etiqueta: String,
    color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = etiqueta, fontSize = 12.sp, color = GraySecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun EstadisticasPerfilPreview() {
    EstadisticasPerfil(
        user = LocalUserProvider.currentUser,
        modifier = Modifier.padding(16.dp)
    )
}

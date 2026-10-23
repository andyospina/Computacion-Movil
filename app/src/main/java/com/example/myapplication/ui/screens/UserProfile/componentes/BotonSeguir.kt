package com.example.myapplication.ui.screens.UserProfile.componentes

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

/** «Seguir» relleno o «Dejar de seguir» con borde, según si el usuario actual ya lo sigue. */
@Composable
fun BotonSeguir(
    siguiendo: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (siguiendo) {
        OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier) {
            Text(text = stringResource(R.string.action_unfollow), color = Color.White)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = Ink)
        ) {
            Text(text = stringResource(R.string.action_follow), fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101012)
@Composable
fun BotonSeguirPreview() {
    BotonSeguir(siguiendo = false, enabled = true, onClick = {})
}

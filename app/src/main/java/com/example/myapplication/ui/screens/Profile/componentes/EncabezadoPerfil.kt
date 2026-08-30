package com.example.myapplication.ui.screens.Profile.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.data.User
import com.example.myapplication.ui.components.InitialsAvatar
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun EncabezadoPerfil(
    user: User,
    modifier: Modifier = Modifier
) {


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(initials = user.initials, size = 64.dp)

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = user.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Miembro desde ${user.memberSinceYear}", color = GraySecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncabezadoPerfilPreview() {
    EncabezadoPerfil(
        user = LocalUserProvider.currentUser,
        modifier = Modifier.padding(16.dp)
    )
}

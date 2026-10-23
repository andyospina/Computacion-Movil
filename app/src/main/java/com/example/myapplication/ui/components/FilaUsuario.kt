package com.example.myapplication.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.data.User
import com.example.myapplication.ui.theme.BorderColor
import com.example.myapplication.ui.theme.GraySecondary

/** Fila de un usuario en las listas de seguidores y seguidos. */
@Composable
fun FilaUsuario(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAsyncImage(profileImage = user.profileImage, size = 40.dp)

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = user.name.ifBlank { user.username }, fontWeight = FontWeight.Bold)
            if (user.username.isNotBlank()) {
                Text(text = "@${user.username}", color = GraySecondary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilaUsuarioPreview() {
    FilaUsuario(user = LocalUserProvider.currentUser.copy(username = "emily"), onClick = {})
}

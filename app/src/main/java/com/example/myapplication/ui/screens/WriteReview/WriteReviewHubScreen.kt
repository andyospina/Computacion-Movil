package com.example.myapplication.ui.screens.WriteReview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.LocalUserProvider
import com.example.myapplication.ui.components.AppLogo
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.InitialsAvatar
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.screens.WriteReview.componentes.BotonEscribirResena
import com.example.myapplication.ui.screens.WriteReview.componentes.DescripcionInicio
import com.example.myapplication.ui.screens.WriteReview.componentes.TituloInicio

@Composable
fun WriteReviewHubScreen(
    modifier: Modifier = Modifier,
    onEscribirResenaClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        BarraSuperior(
            navigation = TopBarNavigation.MENU,
            trailingContent = {
                InitialsAvatar(
                    initials = LocalUserProvider.currentUser.initials,
                    modifier = Modifier.clickable { onAvatarClick() }
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            AppLogo(size = 64.dp)

            Spacer(modifier = Modifier.height(24.dp))

            TituloInicio()

            Spacer(modifier = Modifier.height(12.dp))

            DescripcionInicio(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(40.dp))

            BotonEscribirResena(
                modifier = Modifier.fillMaxWidth(),
                onClick = onEscribirResenaClick
            )
        }
    }
}

@Preview(showBackground = true, name = "WriteReviewHubScreen - Preview")
@Composable
fun WriteReviewHubScreenPreview() {
    WriteReviewHubScreen(
        onEscribirResenaClick = {},
        onAvatarClick = {}
    )
}

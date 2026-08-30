package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Ink

enum class TopBarNavigation {
    NONE,
    MENU,
    BACK
}

@Composable
fun BarraSuperior(
    modifier: Modifier = Modifier,
    title: String = "ReviewLab",
    navigation: TopBarNavigation = TopBarNavigation.MENU,
    onNavigationClick: () -> Unit = {},
    trailingContent: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(ElectricLime)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (navigation) {
            TopBarNavigation.MENU -> {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú",
                    tint = Ink,
                    modifier = Modifier.clickable { onNavigationClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            TopBarNavigation.BACK -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Ink,
                    modifier = Modifier.clickable { onNavigationClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            TopBarNavigation.NONE -> Unit
        }

        AppLogo(size = 28.dp, bubbleColor = Ink, markColor = ElectricLime)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier.weight(1f)
        )

        trailingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun BarraSuperiorPreview() {
    BarraSuperior(
        trailingContent = {
            Text(text = "Acción", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    )
}

@Preview(showBackground = true, name = "BarraSuperior - Back")
@Composable
fun BarraSuperiorBackPreview() {
    BarraSuperior(
        title = "Producto",
        navigation = TopBarNavigation.BACK
    )
}

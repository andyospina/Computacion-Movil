package com.example.myapplication

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ReviewLabApp() {
    val sistemaEnModoOscuro = isSystemInDarkTheme()
    var modoOscuro by rememberSaveable { mutableStateOf(sistemaEnModoOscuro) }

    MyApplicationTheme(darkTheme = modoOscuro) {
        AppNavigation(
            modoOscuro = modoOscuro,
            onModoOscuroChange = { modoOscuro = it }
        )
    }
}

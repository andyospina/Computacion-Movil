package com.example.myapplication.ui.screens.Splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.components.AppLogo
import com.example.myapplication.ui.theme.Ink

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToStart: () -> Unit
) {
    val navigateHome by viewModel.navigateHome.collectAsState()

    LaunchedEffect(navigateHome) {
        when (navigateHome) {
            true -> onNavigateToHome()
            false -> onNavigateToStart()
            null -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Ink),
        contentAlignment = Alignment.Center
    ) {
        AppLogo(size = 96.dp)
    }
}

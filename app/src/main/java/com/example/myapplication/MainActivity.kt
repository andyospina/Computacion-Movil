package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.myapplication.ui.screens.NewReview.NewReviewScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ReviewLabApp()
            }
        }
    }
}

@Composable
fun ReviewLabApp() {
    NewReviewScreen(
        modifier = TODO(),
        onPublicar = TODO(),
        onCancelar = TODO()
    )
}
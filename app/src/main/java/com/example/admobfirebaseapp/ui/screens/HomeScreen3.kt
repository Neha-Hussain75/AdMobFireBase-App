package com.example.admobfirebaseapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import com.example.admobfirebaseapp.ui.components.ScreenWithButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity

    Box( // ✅ Box use kiya taake align kaam kare
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp), // Ad ke liye jagah
            verticalArrangement = Arrangement.Center
        ) {
            ScreenWithButton(
                title = "Home Screen",
                buttonText = "Go to Dashboard"
            ) {
                navController.navigate("dashboard")
            }
        }

        // Banner Ad bottom par fix
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test Ad Unit ID
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter) // ✅ ab kaam karega
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}

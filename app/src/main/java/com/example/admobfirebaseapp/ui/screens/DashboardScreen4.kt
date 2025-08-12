package com.example.admobfirebaseapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.admobfirebaseapp.ui.components.CommonSense
import com.example.admobfirebaseapp.ui.components.LoadingDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity

    var showAdLoading by remember { mutableStateOf(false) }

    // Load Interstitial ad on screen enter
    LaunchedEffect(Unit) {
        CommonSense.loadInterstitial(context)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = "Dashboard Screen")

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                CommonSense.showInterstitial(
                    context,
                    onAdDismissed = {
                        navController.navigate("profile")
                    },
                    onLoading = { showAdLoading = true },
                    onHideLoading = { showAdLoading = false }
                )
            }) {
                Text(text = "Go to Profile")
            }
        }

        // Banner Ad fixed at bottom
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test Banner
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
        )

        // Show loading dialog when ad is loading/showing
        LoadingDialog(show = showAdLoading)
    }
}

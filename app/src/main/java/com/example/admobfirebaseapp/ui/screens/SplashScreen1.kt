package com.example.admobfirebaseapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.admobfirebaseapp.MyApplication
import com.example.admobfirebaseapp.ui.components.LoadingDialog
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as MyApplication
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000) // Splash delay
        val activity = context as? Activity
        if (activity != null) {
            showLoading = true
            app.appOpenAdManager.showAdIfAvailable(activity) {
                showLoading = false
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.White, Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome",
                color = Color.DarkGray,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("login") },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text("Get Started", color = Color.White, fontSize = 18.sp)
            }
        }
    }

    // Loading dialog
    LoadingDialog(show = showLoading, message = "Loading Ad...")
}

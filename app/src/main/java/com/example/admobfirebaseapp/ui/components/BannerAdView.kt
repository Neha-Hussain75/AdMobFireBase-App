package com.example.admobfirebaseapp.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String // ✅ Add this parameter
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId // ✅ Use it here
                loadAd(AdRequest.Builder().build())
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    )
}

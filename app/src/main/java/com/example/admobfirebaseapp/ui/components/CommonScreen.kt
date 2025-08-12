package com.example.admobfirebaseapp.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object CommonSense {

    private var mInterstitialAd: InterstitialAd? = null
    private const val TAG = "CommonSense"

    fun loadInterstitial(context: Context, onLoaded: (() -> Unit)? = null) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712", // Test ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    mInterstitialAd = ad
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Failed to load interstitial ad: ${adError.message}")
                    mInterstitialAd = null
                    onLoaded?.invoke()
                }
            }
        )
    }

    fun showInterstitial(
        context: Context,
        onAdDismissed: () -> Unit,
        onLoading: (() -> Unit)? = null,
        onHideLoading: (() -> Unit)? = null
    ) {
        if (mInterstitialAd != null && context is Activity) {
            onLoading?.invoke() // show loading dialog

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    onHideLoading?.invoke() // hide loading
                    onAdDismissed()
                    mInterstitialAd = null
                    loadInterstitial(context)
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.d(TAG, "Failed to show interstitial ad: ${adError.message}")
                    onHideLoading?.invoke()
                    mInterstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    onHideLoading?.invoke()
                }
            }
            mInterstitialAd?.show(context)
        } else {
            Log.d(TAG, "Interstitial ad not ready")
            onAdDismissed()
        }
    }
}

package com.example.admobfirebaseapp

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenAdManager(private val context: Context) {

    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0
    private var isShowingAd = false

    companion object {
        private const val LOG_TAG = "AppOpenAdManager"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921" // Test ID
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && (Date().time - loadTime) < 4 * 60 * 60 * 1000
    }

    fun loadAd(onLoaded: (() -> Unit)? = null) {
        if (isAdAvailable()) {
            onLoaded?.invoke()
            return
        }

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context, AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(LOG_TAG, "Ad loaded successfully")
                    appOpenAd = ad
                    loadTime = Date().time
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    Log.e(LOG_TAG, "Failed to load ad: ${error.message}")
                    appOpenAd = null
                    onLoaded?.invoke()
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit) {
        if (isAdAvailable()) {
            showAd(activity, onAdDismissed)
        } else {
            loadAd {
                val handler = Handler(Looper.getMainLooper())
                val checkRunnable = object : Runnable {
                    override fun run() {
                        if (isAdAvailable()) {
                            showAd(activity, onAdDismissed)
                        } else {
                            handler.postDelayed(this, 300)
                        }
                    }
                }
                handler.post(checkRunnable)
            }
        }
    }

    private fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (isShowingAd) return

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(LOG_TAG, "Ad dismissed")
                appOpenAd = null
                isShowingAd = false
                loadAd()
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                Log.e(LOG_TAG, "Failed to show ad: ${error.message}")
                isShowingAd = false
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(LOG_TAG, "Ad is showing")
                isShowingAd = true
            }
        }

        appOpenAd?.show(activity)
    }
}

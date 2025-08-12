package com.example.admobfirebaseapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.FirebaseDatabase

object AdManager {
    private const val TAG = "AdManager"

    // default test IDs (fallback)
    var bannerId: String = "ca-app-pub-3940256099942544/6300978111"
    private var interstitialId: String = "ca-app-pub-3940256099942544/1033173712"
    private var appOpenId: String = "ca-app-pub-3940256099942544/3419835294"

    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null

    /** Call early (MainActivity or MyApplication) */
    fun initAndLoadIds(context: Context) {
        // try read from Firebase Realtime DB node "adUnitIds"
        val ref = FirebaseDatabase.getInstance().getReference("adUnitIds")
        ref.get().addOnSuccessListener { snap ->
            snap.child("banner")?.value?.toString()?.let { bannerId = it }
            snap.child("interstitial")?.value?.toString()?.let { interstitialId = it }
            snap.child("appOpen")?.value?.toString()?.let { appOpenId = it }
            Log.d(TAG, "Ad ids loaded from Firebase")
            loadInterstitial(context)
            loadAppOpen(context)
        }.addOnFailureListener {
            Log.w(TAG, "Failed to load ad ids, using defaults: ${it.message}")
            // fallback: still try to preload with defaults
            loadInterstitial(context)
            loadAppOpen(context)
        }
    }

    /** Interstitial loading */
    fun loadInterstitial(context: Context) {
        val req = AdRequest.Builder().build()
        InterstitialAd.load(context, interstitialId, req, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d(TAG, "Interstitial loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                Log.d(TAG, "Interstitial failed to load: ${error.message}")
            }
        })
    }

    /**
     * Show interstitial if loaded. Provide activity (LocalContext.current as? Activity).
     * onComplete called after ad dismissed or if no ad.
     */
    fun showInterstitial(activity: Activity?, onComplete: () -> Unit) {
        if (activity == null) {
            onComplete()
            return
        }
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity) // preload next
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onComplete()
                }
            }
            ad.show(activity)
        } else {
            // not loaded => proceed
            onComplete()
        }
    }

    /** Basic App Open loading (optional) */
    fun loadAppOpen(context: Context) {
        val req = AdRequest.Builder().build()
        AppOpenAd.load(context, appOpenId, req, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(TAG, "AppOpen loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    Log.d(TAG, "AppOpen failed: ${loadAdError.message}")
                }
            })
    }

    /** Show App Open ad if available; always call onComplete afterwards */
    fun showAppOpen(activity: Activity?, onComplete: () -> Unit) {
        if (activity == null) { onComplete(); return }
        val ad = appOpenAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    loadAppOpen(activity)
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    appOpenAd = null
                    onComplete()
                }
            }
            ad.show(activity)
        } else {
            onComplete()
        }
    }
}

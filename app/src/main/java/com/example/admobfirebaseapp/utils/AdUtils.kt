package com.example.admobfirebaseapp.utils

import android.app.Activity
import com.example.admobfirebaseapp.MyApplication
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun MyApplication.showAdIfAvailableSuspend(activity: Activity) =
    suspendCancellableCoroutine<Unit> { cont ->
        this.appOpenAdManager.showAdIfAvailable(activity) {
            cont.resume(Unit)
        }
    }

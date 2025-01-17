package com.pin.util.admob

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pin.util.BuildConfig

class Interstitial(
    private val adUnitId: String,
) {

    private var interstitialAd: InterstitialAd? = null
    private var callback: FullScreenContentCallback? = null

    fun show(activity: Activity, state: InterstitialAdStateAction) {
        state.onLoading()
        val id = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else adUnitId
        InterstitialAd.load(
            activity,
            id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    callback = state

                    ad.fullScreenContentCallback = state

                    state.onLoadComplete()

                    ad.show(activity)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    println("InterstitialAd failed to load: $adError")
                    state.onLoadFailed(adError)
                }
            })
    }
}


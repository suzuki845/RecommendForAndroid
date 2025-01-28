package com.pin.util.admob

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem

class InterstitialAdStateAction(
    val onLoading: () -> Unit,
    val onLoadComplete: () -> Unit,
    val onLoadFailed: (LoadAdError) -> Unit,
    val onShowComplete: () -> Unit,
    val onShowFailed: (AdError) -> Unit
) : FullScreenContentCallback() {
    override fun onAdShowedFullScreenContent() {
        println("onAdShowedFullScreenContent.")
    }

    override fun onAdDismissedFullScreenContent() {
        println("onAdDismissedFullScreenContent.")
        onShowComplete()
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        println("onAdFailedToShowFullScreenContent: $adError")
        onShowFailed(adError)
    }

    override fun onAdImpression() {
        println("impression occurred.")
    }
}

class RewardAdStateAction(
    val onLoading: () -> Unit,
    val onLoadComplete: () -> Unit,
    val onLoadFailed: (LoadAdError) -> Unit,
    val onShowComplete: () -> Unit,
    val onShowFailed: (AdError) -> Unit,
    val onUserEarnedReward: (RewardItem) -> Unit
) : FullScreenContentCallback() {
    override fun onAdShowedFullScreenContent() {
        println("onAdShowedFullScreenContent.")
    }

    override fun onAdDismissedFullScreenContent() {
        println("onAdDismissedFullScreenContent.")
        onShowComplete()
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        println("onAdFailedToShowFullScreenContent: $adError")
        onShowFailed(adError)
    }

    override fun onAdImpression() {
        println("impression occurred.")
    }

}
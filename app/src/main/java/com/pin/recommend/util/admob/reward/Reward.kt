package com.pin.util.admob.reward

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pin.recommend.BuildConfig
import com.pin.util.admob.RewardAdStateAction


class Reward(private val adUnitId: String) {
    companion object {
        const val TAG = "com.suzuki.util.Reward"
    }

    private var rewardedAd: RewardedAd? = null

    private var callback: FullScreenContentCallback? = null

    private var testId = "ca-app-pub-3940256099942544/1712485313"

    fun show(activity: Activity, state: RewardAdStateAction) {
        state.onLoading()
        val adRequest = AdRequest.Builder().build()
        val id = if (BuildConfig.DEBUG) testId else adUnitId
        RewardedAd.load(activity, id, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                state.onLoadFailed(adError)
                println("$TAG -> Ad Load failed.")
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                callback = state
                ad.fullScreenContentCallback = state

                state.onLoadComplete()

                ad.show(activity) { rewardItem ->
                    state.onUserEarnedReward(rewardItem)
                }
                println("$TAG -> Ad Loaded.")
            }
        })
    }


}



package com.pin.recommend.ui.component.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.pin.recommend.BuildConfig

@Composable
fun ComposableAdaptiveBanner(adId: String) {
    var id = adId
    if (BuildConfig.DEBUG) {
        id = "ca-app-pub-3940256099942544/6300978111"
    }
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = id
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
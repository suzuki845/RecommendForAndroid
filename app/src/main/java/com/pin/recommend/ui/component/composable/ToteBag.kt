package com.pin.recommend.ui.component.composable

import android.graphics.Bitmap
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.pin.recommend.ui.component.ToteBagView


@Composable
fun ToteBagViewComposable(
    badges: List<Bitmap> = emptyList(),
    width: Int = 800,
    height: Int = 800
) {
    val params = FrameLayout.LayoutParams(
        width,
        height
    )
    AndroidView(
        factory = { context ->
            ToteBagView(context).apply {
                this.badges = badges
                this.layoutParams = params
            }
        },
        update = { toteBagView ->
            toteBagView.badges = badges
            toteBagView.layoutParams = params
        }
    )
}
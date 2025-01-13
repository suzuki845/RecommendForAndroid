package com.pin.recommend.ui.component.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.TextSliderView
import java.io.File


@Composable
fun Slideshow(
    modifier: Modifier = Modifier,
    images: List<File>,
    currentIndex: Int,
) {

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val sliderLayout = SliderLayout(ctx)
            for (image in images) {
                val textSliderView = TextSliderView(ctx)
                textSliderView
                    .image(image)
                    .setRequestOption(RequestOptions())
                    .setProgressBarVisible(true)
                sliderLayout.addSlider(textSliderView)
            }
            sliderLayout.setCurrentPosition(currentIndex, true)
            sliderLayout
        },
    )
}


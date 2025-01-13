package com.pin.recommend.ui.story

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pin.recommend.R
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.component.composable.Slideshow
import java.io.File


class StorySlideShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(StoryDetailActivity.INTENT_SLIDE_SHOW) ?: ""
        val story = StoryWithPictures.fromJson(json)
        val initPosition =
            intent.getIntExtra(StoryDetailActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION, 0)

        val self = this
        setContent {
            Body(
                story.pictures.map { it.getFile(self) },
                initPosition
            )
        }
    }

    @Composable
    fun Body(images: List<File>, pos: Int) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text("スライドショー")
                    },
                )
            },
            bottomBar = {
                Column {
                    ComposableAdaptiveBanner(adId = resources.getString(R.string.banner_id))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Slideshow(
                    images = images,
                    currentIndex = pos
                )
            }
        }
    }

}
package com.pin.recommend.ui.story

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pin.recommend.R
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.story.StoryEditActivity.Companion.INTENT_EDIT_ENTITY

class StoryDetailActivity : AppCompatActivity() {

    private val vm: StoryDetailsViewModel by lazy {
        ViewModelProvider(this)[StoryDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra(INTENT_STORY_DETAILS)
        val entity = Gson().fromJson(json, StoryWithPictures::class.java)!!

        vm.setEntity(entity)
        vm.subscribe(this)

        setContent {
            Body(
                vm = vm,
                state = vm.state.collectAsState(StoryDetailsViewModelState()).value
            )
        }
    }

    @Composable
    fun Body(
        vm: StoryDetailsViewModel,
        state: StoryDetailsViewModelState,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text("ストーリー")
                    },
                    actions = {
                        TextButton({
                            toEdit(state)
                        }) {
                            Text("編集")
                        }
                    },
                )
            },
            bottomBar = {
                Column {
                    ComposableAdaptiveBanner(adId = resources.getString(R.string.banner_id))
                }
            }
        ) { padding ->
            ErrorMessage(vm, state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Content(state)
            }
        }
    }

    @Composable
    fun Content(
        state: StoryDetailsViewModelState,
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Date(state)
            Spacer(Modifier.height(8.dp))
            PictureList(state)
            Comment(state)
        }
    }

    @Composable
    fun Date(
        state: StoryDetailsViewModelState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(start = 16.dp, top = 16.dp))
        ) {
            Text(
                text = state.formattedDate ?: ""
            )
        }
    }

    @Composable
    fun PictureList(
        state: StoryDetailsViewModelState
    ) {
        val self = this
        LazyRow {
            items(state.storyWithPictures?.pictures ?: arrayListOf()) { picture ->
                picture.getBitmap(self, 100, 100)?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(100.dp)
                            .clickable {
                                val intent = Intent(self, StorySlideShowActivity::class.java)
                                intent.putExtra(
                                    INTENT_SLIDE_SHOW,
                                    state.storyWithPictures?.toJson()
                                )
                                intent.putExtra(
                                    INTENT_SLIDE_SHOW_CURRENT_POSITION,
                                    state.pictures?.indexOf(picture)
                                )
                                self.startActivity(intent)
                            }
                    )
                }
            }
        }
    }

    @Composable
    fun Comment(
        state: StoryDetailsViewModelState
    ) {
        Text(fontSize = 18.sp, text = state.storyWithPictures?.story?.comment ?: "")
    }


    @Composable
    fun ErrorMessage(
        vm: StoryDetailsViewModel,
        state: StoryDetailsViewModelState
    ) {
        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { vm.resetError() },
                title = { Text("Error") },
                text = { Text(state.errorMessage) },
                confirmButton = {
                    TextButton(onClick = { vm.resetError() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    private fun toEdit(
        state: StoryDetailsViewModelState
    ) {
        val intent = Intent(this@StoryDetailActivity, StoryEditActivity::class.java)
        intent.putExtra(INTENT_EDIT_ENTITY, state.storyWithPictures?.toJson())
        startActivity(intent)
    }

    companion object {
        const val INTENT_STORY_DETAILS =
            "com.pin.recommend.view.story.StoryDetailActivity.INTENT_STORY_DETAILS"
        const val INTENT_UPDATED_STORY =
            "com.pin.recommend.view.story.StoryDetailActivity.INTENT_UPDATED_STORY"
        const val INTENT_SLIDE_SHOW = "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW"
        const val INTENT_SLIDE_SHOW_CURRENT_POSITION =
            "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION"
    }
}
package com.pin.recommend.ui.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.util.toFormattedString
import java.util.Calendar

@Composable
fun StoryListComponent(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState
) {
    Column(
        Modifier
            .drawBehind { // 親の背景を描画
                drawRect(Color.White.copy(alpha = 0.5f))
            }
    ) {
        SortOrder(vm, state)
        List(vm, state)
    }

}

@Composable
fun List(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState
) {
    LazyColumn {
        items(state.stories) {
            ListItem(vm, state, it)
            Divider()
        }
    }
}

@Composable
fun SortOrder(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
) {
    var expanded by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { androidx.compose.material.Text("作成日　新しい準") },
            onClick = {
                vm.updateStorySortOrder(0)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { androidx.compose.material.Text("作成日　古い順") },
            onClick = {
                vm.updateStorySortOrder(1)
                expanded = false
            }
        )
    }

    TextButton({
        expanded = true
    }) {
        Row {
            Text("並び順 : ")
            Text(
                if (state.storySortOrder == 0) {
                    "新しい順"
                } else {
                    "古い順"
                }
            )
        }
    }
}

@Composable
fun ListItem(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
    story: StoryWithPictures
) {
    val calendar = Calendar.getInstance()
    val elapsedDay = story.story.getDiffDays(calendar)
    val elapsedText = if (elapsedDay < 0) {
        (-elapsedDay).toString() + "日後"
    } else {
        if (elapsedDay == 0L) {
            "今日"
        } else {
            elapsedDay.toString() + "日前"
        }
    }

    val activity = LocalContext.current as AppCompatActivity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(activity, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.INTENT_STORY_DETAILS, story.toJson())
                activity.startActivity(intent)
            }) {
        Column {
            Text(
                fontSize = 24.sp,
                text = elapsedText
            )
            Text(
                text = story.story.created?.toFormattedString() ?: ""
            )
            Text(
                text = story.story.getShortComment(15)
            )
            LazyRow(
                Modifier
                    .fillMaxWidth(0.7f)
            ) {
                items(story.pictures) {
                    it.getBitmap(activity, 60, 60)?.asImageBitmap()?.let { image ->
                        Image(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(60.dp),
                            bitmap = image,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (state.isDeleteModeStories) {
            DeleteButton(activity, vm, story)
        }
    }
}

@Composable
fun DeleteButton(
    activity: AppCompatActivity,
    vm: CharacterDetailsViewModel,
    story: StoryWithPictures
) {
    IconButton({
        val dialog =
            DeleteDialogFragment(object :
                DialogActionListener<DeleteDialogFragment> {
                override fun onDecision(dialog: DeleteDialogFragment) {
                    vm.deleteStory(story.story)
                }

                override fun onCancel() {}
            })
        dialog.show(activity.supportFragmentManager, DeleteDialogFragment.Tag)
    }) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Delete),
            contentDescription = null,
            modifier = Modifier.padding(4.dp),
        )
    }
}

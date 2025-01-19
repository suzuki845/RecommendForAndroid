package com.pin.recommend.ui.story

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.pin.recommend.MyApplication
import com.pin.recommend.R
import com.pin.recommend.domain.model.StoryEditorAction
import com.pin.recommend.domain.model.StoryEditorStatus
import com.pin.recommend.ui.component.DatePickerModal
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.util.PermissionChecker
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.toFormattedString
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import java.time.Instant
import java.util.Date

@Composable
fun Body(
    title: String,
    activity: AppCompatActivity,
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState,
    requestCodePicture: Int,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.Black,
                title = {
                    Text(title)
                },
                actions = {
                    TextButton({
                        save(activity, vm, state)
                    }) {
                        Text("保存")
                    }
                },
            )
        },
        bottomBar = {
            Column {
                PickImage(activity, requestCodePicture)
                ComposableAdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
            }
        }
    ) { padding ->
        ErrorMessage(vm, state)
        SaveSuccess(activity, state)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Content(activity, vm, state)
        }
    }
}

@Composable
fun SaveSuccess(activity: AppCompatActivity, state: StoryEditorViewModelState) {
    if (state.action == StoryEditorAction.Save && state.status == StoryEditorStatus.Success) {
        activity.finish()
    }
}

@Composable
fun ErrorMessage(
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState
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

@Composable
fun Content(
    activity: AppCompatActivity,
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState,
) {
    Column {
        DatePickButton(activity, vm, state)
        Spacer(Modifier.height(8.dp))
        PictureList(vm, state)
        Comment(vm, state)
    }
}

@Composable
fun DatePickButton(
    activity: AppCompatActivity,
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState
) {
    var showModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, top = 16.dp))
            .clickable {
                showModal = true
            }
    ) {
        Text(
            color = MaterialTheme.colors.primary,
            text = state.created.toFormattedString()
        )
    }

    if (showModal) {
        DatePickerModal(
            initialValue = state.created,
            onDateSelected = {
                if (it != null) {
                    vm.setCreated(Date.from(Instant.ofEpochMilli(it)))
                }
            },
            onDismiss = { showModal = false }
        )
    }

}

@Composable
fun PictureList(
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int>(-1) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("削除") },
            onClick = {
                vm.removePicture(selectedIndex)
                expanded = false
            }
        )
    }

    LazyRow {
        items(state.pictures) { picture ->
            picture.bitmap?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(100.dp)
                        .clickable {
                            selectedIndex = state.pictures.indexOf(picture)
                            expanded = true
                        }
                )
            }
        }
    }
}

@Composable
fun Comment(
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
        ),
        value = state.comment,
        placeholder = {
            Text("ここに入力してください")
        },
        onValueChange = {
            vm.setComment(it)
        },
    )

}

@Composable
fun PickImage(
    activity: AppCompatActivity,
    requestCodePicture: Int
) {
    Row {
        IconButton({
            onPickImage(activity, requestCodePicture)
        }) {
            Icon(Icons.Default.Image, contentDescription = "", modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}


fun onPickImage(activity: AppCompatActivity, requestCodePicture: Int) {
    if (!PermissionChecker.requestPermissions(
            activity, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
        )
    ) {
        return
    }

    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "image/*"
    activity.startActivityForResult(intent, requestCodePicture)
}

private fun save(
    activity: AppCompatActivity,
    vm: StoryEditorViewModel,
    state: StoryEditorViewModelState
) {
    val ad = Interstitial(activity.resources.getString(R.string.interstitial_f_id))
    val progress = ProgressDialog(activity).apply {
        setTitle("少々お待ちください...")
        setCancelable(false)
    }
    ad.show(activity, InterstitialAdStateAction({
        progress.show()
    }, {
        progress.dismiss()
    }, {
        progress.dismiss()
        vm.save()
    }, {
        vm.save()
    }, {
        progress.dismiss()
        vm.save()
    }))
}

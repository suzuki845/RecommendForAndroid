package com.pin.recommend.ui.anniversary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pin.recommend.R
import com.pin.recommend.domain.model.AnniversaryEditorAction
import com.pin.recommend.domain.model.AnniversaryEditorStatus
import com.pin.recommend.ui.anniversary.AnniversaryCreateActivity.Companion.INTENT_CREATE_ANNIVERSARY
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.ui.character.convertMillisToDate
import com.pin.recommend.ui.component.DatePickerModal
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import java.time.Instant
import java.util.Date


@Composable
fun Body(
    title: String,
    activity: AppCompatActivity,
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
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
                        vm.done()
                    }) {
                        Text("完了")
                    }
                },
            )
        },
        bottomBar = {
            ComposableAdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
        }
    ) { padding ->
        ErrorMessage(vm, state)
        SaveSuccess(activity, state)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            ErrorMessage(vm, state)
            SaveSuccess(activity, state)
            Content(vm, state)
        }
    }
}


@Composable
fun ErrorMessage(vm: AnniversaryEditorViewModel, state: AnniversaryEditorViewModelState) {
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
fun SaveSuccess(activity: AppCompatActivity, state: AnniversaryEditorViewModelState) {
    if (state.action == AnniversaryEditorAction.Create && state.status == AnniversaryEditorStatus.Success) {
        val resultIntent = Intent()
        resultIntent.putExtra(INTENT_CREATE_ANNIVERSARY, state.toDraft().toJson())
        activity.setResult(RESULT_OK, resultIntent)
        activity.finish()
    } else if (state.action == AnniversaryEditorAction.Update && state.status == AnniversaryEditorStatus.Success) {
        val resultIntent = Intent()
        resultIntent.putExtra(INTENT_EDIT_ANNIVERSARY, state.toDraft().toJson())
        activity.setResult(RESULT_OK, resultIntent)
        activity.finish()
    }
}

@Composable
fun Content(
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
) {
    Column {
        Title(vm, state)
        Divider()
        Date(vm, state)
        Divider()
        TopText(vm, state)
        Divider()
        BottomText(vm, state)
    }
}

@Composable
fun Title(
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
) {
    Column {
        Section { Text("記念日名") }
        TextField(
            modifier = Modifier.padding(4.dp),
            placeholder = { Text("誕生日") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            value = state.name,
            onValueChange = { vm.setName(it) })
    }
}

@Composable
fun Date(
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
) {
    var showModal by remember { mutableStateOf(false) }

    Column {
        Section { Text("日付") }
        TextField(
            readOnly = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            value = convertMillisToDate(state.date.toInstant().toEpochMilli()),
            onValueChange = { },
            placeholder = { Text("MM/DD/YYYY") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(state.date) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showModal = true
                        }
                    }
                }
        )
    }
    if (showModal) {
        DatePickerModal(
            initialValue = state.date,
            onDateSelected = {
                if (it != null) {
                    vm.setDate(Date.from(Instant.ofEpochMilli(it)))
                }
            },
            onDismiss = { showModal = false }
        )
    }
}


@Composable
fun TopText(
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
) {
    Column {
        Section { Text("上のテキスト") }
        TextField(
            modifier = Modifier.padding(4.dp),
            placeholder = { Text("生まれて") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ), value = state.topText, onValueChange = { vm.setTopText(it) })
    }
}


@Composable
fun BottomText(
    vm: AnniversaryEditorViewModel,
    state: AnniversaryEditorViewModelState
) {
    Column {
        Section { Text("下のテキスト") }
        TextField(
            modifier = Modifier.padding(4.dp),
            placeholder = { Text("になりました") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            value = state.bottomText,
            onValueChange = { vm.setBottomText(it) })
    }
}


package com.pin.recommend.ui.event

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pin.recommend.R
import com.pin.recommend.domain.model.EventEditorAction
import com.pin.recommend.domain.model.EventEditorStatus
import com.pin.recommend.ui.component.DatePickerTextField
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction


@Composable
fun Body(
    title: String,
    activity: AppCompatActivity,
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
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
                        save(activity, vm)
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
        SaveSuccess(activity, vm, state)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Content(vm, state)
        }
    }
}


@Composable
fun ErrorMessage(
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
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
fun SaveSuccess(
    activity: AppCompatActivity,
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
) {
    if (state.action == EventEditorAction.Save && state.status == EventEditorStatus.Success) {
        activity.finish()
    }
}


@Composable
fun Content(
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
) {
    Column {
        Date(vm, state)
        Title(vm, state)
        Memo(vm, state)
    }
}

@Composable
fun Date(
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
) {
    Column {
        Section { Text("日付") }
        DatePickerTextField(value = state.date, onDateSelected = {
            if (it != null) {
                vm.setDate(it)
            }
        })
    }
}

@Composable
fun Title(
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
) {
    Column {
        Section { Text("タイトル") }
        TextField(modifier = Modifier.padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ), value = state.title, onValueChange = { vm.setTitle(it) })
    }
}

@Composable
fun Memo(
    vm: EventEditorViewModel,
    state: EventEditorViewModelState
) {
    Column {
        Section { Text("メモ") }
        TextField(modifier = Modifier.padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ), value = state.memo, onValueChange = { vm.setMemo(it) })
    }
}

private fun save(activity: AppCompatActivity, vm: EventEditorViewModel) {
    val ad = Interstitial(activity.getString(R.string.interstitial_f_id))
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


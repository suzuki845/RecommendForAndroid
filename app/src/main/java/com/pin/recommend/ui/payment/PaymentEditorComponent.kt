package com.pin.recommend.ui.payment

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pin.recommend.R
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.domain.model.PaymentEditorAction
import com.pin.recommend.domain.model.PaymentEditorStatus
import com.pin.recommend.ui.component.DatePickerTextField
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction

@Composable
fun Body(
    title: String,
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
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
                        save(
                            activity = activity,
                            vm = vm,
                            state = state
                        )
                    }) {
                        Text("保存")
                    }
                },
            )
        },
        bottomBar = {
            AdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
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
fun SaveSuccess(activity: AppCompatActivity, state: PaymentEditorViewModelState) {
    if (state.action == PaymentEditorAction.Save && state.status == PaymentEditorStatus.Success) {
        activity.finish()
    }
}

@Composable
fun ErrorMessage(
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
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
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Type(vm, state)
        Divider()
        Date(activity = activity, vm, state)
        Divider()
        Amount(vm, state)
        Divider()
        Memo(vm, state)
        Divider()
        Tag(activity = activity, vm, state)
    }
}

@Composable
fun Type(
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val unselected = ButtonDefaults.buttonColors().contentColor(false).value
    val selected = MaterialTheme.colors.primary
    val payColor = if (state.type == 0) {
        selected
    } else {
        unselected
    }
    val savingsColor = if (state.type == 1) {
        selected
    } else {
        unselected
    }
    Column {
        Section { Text("Payか貯金の選択") }
        Row {
            Spacer(Modifier.weight(1f))
            TextButton({
                vm.setType(0)
            }) {
                Text(color = payColor, text = "Pay")
            }
            TextButton({
                vm.setType(1)
            }) {
                Text(color = savingsColor, text = "貯金")
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun Date(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
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
fun Amount(
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    Column {
        Section { Text("金額") }
        TextField(
            modifier = Modifier.padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = state.amount.toString(),
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    val newAmount = newValue.toIntOrNull() ?: 0
                    vm.setAmount(newAmount)
                }
            })
    }
}

@Composable
fun Memo(
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    Column {
        Section { Text("メモ") }
        TextField(
            modifier = Modifier.padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            value = state.memo,
            onValueChange = { newValue ->
                vm.setMemo(newValue)
            })
    }
}

@Composable
fun Tag(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val openTafDialog = remember { mutableStateOf(false) }

    Column {
        Section {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(22.dp)
            ) {
                Text("タグ")
                Spacer(Modifier.weight(1f))
                TextButton(contentPadding = PaddingValues(0.dp), onClick = {
                    toTagListActivity(activity = activity, vm = vm, state = state)
                }) {
                    Text("編集")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(start = 16.dp, top = 16.dp))
                .clickable {
                    openTafDialog.value = true
                }
        ) {
            Text(
                color = MaterialTheme.colors.primary,
                text = state.selectedTag?.tagName ?: "タップして選択"
            )
        }
    }

    if (openTafDialog.value) {
        TagDialog(state = state, onItemSelected = {
            vm.setTag(it)
            openTafDialog.value = false
        }, onDismissRequest = {
            openTafDialog.value = false
        })
    }
}


fun toTagListActivity(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val intent = Intent(activity, PaymentTagListActivity::class.java);
    intent.putExtra(PaymentTagListActivity.INTENT_PAYMENT_TYPE, state.type)
    activity.startActivity(intent)
}


@Composable
fun TagDialog(
    state: PaymentEditorViewModelState,
    onItemSelected: (PaymentTag) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                LazyColumn(Modifier.weight(0.9f)) {
                    items(state.currentTags) { tag ->
                        TextButton({
                            onItemSelected(tag)
                        }) {
                            Text(tag.tagName)
                        }
                    }
                }
                Row {
                    Spacer(Modifier.weight(1f))
                    TextButton({ onDismissRequest() }) {
                        Text("閉じる")
                    }
                }
            }
        }
    }
}

private fun save(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
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


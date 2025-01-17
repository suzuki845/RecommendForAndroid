package com.pin.recommend.ui.payment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pin.recommend.R
import com.pin.recommend.domain.model.PaymentEditorAction
import com.pin.recommend.domain.model.PaymentEditorStatus
import com.pin.recommend.ui.adapter.PaymentTagAdapter
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import com.pin.recommend.util.TimeUtil
import com.pin.recommend.util.toFormattedString
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import java.util.Calendar

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
        TextField(
            modifier = Modifier
                .padding(4.dp)
                .pointerInput(state.date) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            onShowDatePickerDialog(activity = activity, vm = vm, state = state)
                        }
                    }
                },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            readOnly = true,
            value = state.date.toFormattedString(),
            onValueChange = {})
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
                    onShowTagDialog(activity = activity, vm = vm, state = state)
                }
        ) {
            Text(
                color = MaterialTheme.colors.primary,
                text = state.selectedTag?.tagName ?: "タップして選択"
            )
        }
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

fun onShowTagDialog(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val listView = ListView(activity)
    val tagAdapter = PaymentTagAdapter(activity, onDelete = {})
    tagAdapter.setList(state.currentTags)
    listView.adapter = tagAdapter

    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        .setTitle("選択してくだい。")
        .setView(listView)
    builder.setNegativeButton("キャンセル") { d, _ ->
        d.cancel()
    }

    val dialog = builder.create()
    listView.setOnItemClickListener { parent, view, pos, id ->
        val tag = tagAdapter.getItem(pos)
        vm.setTag(tag)
        dialog.cancel()
    }

    dialog.show()
}


fun onShowDatePickerDialog(
    activity: AppCompatActivity,
    vm: PaymentEditorViewModel,
    state: PaymentEditorViewModelState
) {
    val calendar = Calendar.getInstance()
    calendar.time = state.date
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val datePickerDialog = DatePickerDialog(
        activity,
        DatePickerDialog.OnDateSetListener { dialog, year, month, dayOfMonth ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                !dialog.isShown
            ) {
                return@OnDateSetListener
                //api19はクリックするとonDateSetが２回呼ばれるため
            }
            val newCalender = Calendar.getInstance()
            newCalender[year, month] = dayOfMonth
            TimeUtil.resetTime(newCalender)
            val date = newCalender.time
            vm.setDate(date)
        },
        year,
        month,
        dayOfMonth
    )
    datePickerDialog.show()
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


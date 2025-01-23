package com.pin.recommend.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.model.DateWithPayments
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.Section
import com.pin.recommend.ui.main.PaymentListFragment.Companion.TAG
import com.pin.recommend.util.toFormattedString
import com.pin.recommend.util.toMdString


@Composable
fun PaymentListComponent(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    Column(
        modifier = Modifier
            .drawBehind { // 親の背景を描画
                drawRect(Color.White.copy(alpha = 0.5f))
            }
    ) {
        DateSelector(vm, state)
        Amount(vm, state)
        List(vm, state)
    }
}

@Composable
fun DateSelector(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton({
            vm.prevPaymentMonth()
        }) {
            Icon(Icons.Default.ChevronLeft, null, tint = MaterialTheme.colors.primary)
        }
        Spacer(Modifier.weight(1f))
        Text(fontSize = 20.sp, text = state.payments.selectedDate.toFormattedString())
        Spacer(Modifier.weight(1f))
        IconButton({
            vm.nextPaymentMonth()
        }) {
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun Amount(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(fontSize = 16.sp, text = "今月のPay: ${state.payments.totalPayment}円")
        TextButton({
            val intent = Intent(context, PaymentWholePeriodActivity::class.java)
            intent.putExtra(
                PaymentWholePeriodActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER,
                state.character?.id
            )
            context.startActivity(intent)
        }) {
            Text("全期間のPayを見る")
        }

        Text(fontSize = 16.sp, text = "今月の貯金: ${state.payments.totalSavings}円")
        TextButton({
            val intent = Intent(context, SavingsWholePeriodActivity::class.java)
            intent.putExtra(
                SavingsWholePeriodActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER,
                state.character?.id
            )
            context.startActivity(intent)
        }) {
            Text("全期間の貯金を見る")
        }

    }
}

@Composable
fun List(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    LazyColumn {
        items(state.payments.monthlyData.result) {
            Item(vm, state, it)
        }
    }
}

@Composable
fun Item(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
    item: DateWithPayments
) {
    val activity = LocalContext.current as AppCompatActivity
    Column {
        Section {
            Text(item.date.toMdString())
        }
        repeat(item.payments.size) { i ->
            item.payments.getOrNull(i)?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    Column {
                        Text(
                            fontSize = 16.sp,
                            color = if (it.payment.type == 0) Color.Red else Color.Blue,
                            text = it.payment.amount.toInt().toString() + "円"
                        )
                        Text("タグ: ${it.payment.paymentTag?.tagName ?: ""}")
                        Text("メモ: ${it.payment.memo ?: ""}")
                    }
                    Spacer(Modifier.weight(1f))
                    if (state.isDeleteModePayments) {
                        IconButton({
                            deletePayment(vm, activity, it.payment)
                        }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
            Divider()
        }
    }
}

private fun deletePayment(
    vm: CharacterDetailsViewModel,
    context: AppCompatActivity,
    item: Payment
) {
    val dialog =
        DeleteDialogFragment(object :
            DialogActionListener<DeleteDialogFragment> {
            override fun onDecision(dialog: DeleteDialogFragment) {
                vm.deletePayment(item)
            }

            override fun onCancel() {
            }
        })
    dialog.show(context.supportFragmentManager, TAG)
}
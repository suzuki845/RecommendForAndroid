package com.pin.recommend.ui.payment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R

class PaymentEditActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EDIT_PAYMENT = "com.pin.recommend.EDITPaymentActivity.INTENT_EDIT_PAYMENT"
    }

    private val vm: PaymentEditorViewModel by lazy {
        ViewModelProvider(this)[PaymentEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val id = intent.getLongExtra(INTENT_EDIT_PAYMENT, -1);
        if (id != -1L) {
            vm.setEntityById(id)
        }
        vm.subscribe(this)
        setContent {
            Body(
                title = "編集",
                activity = this,
                vm = vm,
                state = vm.state.collectAsState(PaymentEditorViewModelState()).value
            )
        }
    }


}

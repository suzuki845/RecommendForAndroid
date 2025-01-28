package com.pin.recommend.ui.payment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider

class PaymentWholePeriodActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER =
            "com.pin.recommend.WholePeriodPaymentActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER"
    }

    private val vm by lazy {
        ViewModelProvider(this)[PaymentWholePeriodViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER, -1))
        vm.observe(this)

        setContent {
            PaymentWholePeriodComponent(
                0,
                vm,
                vm.state.collectAsState(PaymentWholePeriodViewModelState()).value
            )
        }
    }


}
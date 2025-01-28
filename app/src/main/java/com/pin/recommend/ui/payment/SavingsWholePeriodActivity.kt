package com.pin.recommend.ui.payment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider

class SavingsWholePeriodActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER =
            "com.pin.recommend.WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER"
    }

    private val vm by lazy {
        ViewModelProvider(this)[PaymentWholePeriodViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER, -1))
        vm.observe(this)

        setContent {
            PaymentWholePeriodComponent(
                1,
                vm,
                vm.state.collectAsState(PaymentWholePeriodViewModelState()).value
            )
        }
    }


}
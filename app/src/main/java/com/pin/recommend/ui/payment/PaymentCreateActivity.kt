package com.pin.recommend.ui.payment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.ui.component.composable.Body


class PaymentCreateActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_PAYMENT =
            "com.pin.recommend.CreatePaymentActivity.INTENT_CREATE_PAYMENT"
    }

    private val vm: PaymentEditorViewModel by lazy {
        ViewModelProvider(this)[PaymentEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val characterId = intent.getLongExtra(INTENT_CREATE_PAYMENT, 0L)
        vm.setId(0)
        vm.setCharacterId(characterId)
        vm.subscribe(this)

        setContent {
            Body(
                title = "作成",
                activity = this,
                vm = vm,
                state = vm.state.collectAsState(PaymentEditorViewModelState()).value
            )
        }
    }


}

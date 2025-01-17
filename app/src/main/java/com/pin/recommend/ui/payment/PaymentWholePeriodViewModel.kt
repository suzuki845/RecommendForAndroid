package com.pin.recommend.ui.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.model.WholePeriodCharacterPaymentModel
import com.pin.recommend.util.combine3
import kotlinx.coroutines.flow.Flow

class PaymentWholePeriodViewModelState(
    val characterName: String = "",
    val appearance: Appearance = Appearance(),
    val paymentAmount: Int = 0,
    val savingsAmount: Int = 0
)

class PaymentWholePeriodViewModel(private val application: Application) :
    AndroidViewModel(application) {

    private val model by lazy {
        WholePeriodCharacterPaymentModel(application)
    }

    private val _state = combine3(
        model.character,
        model.wholePeriodPaymentAmount,
        model.wholePeriodSavingsAmount
    ) { a, b, c ->
        PaymentWholePeriodViewModelState(
            characterName = a?.name ?: "",
            appearance = a?.appearance(application) ?: Appearance(),
            paymentAmount = b ?: 0,
            savingsAmount = c ?: 0
        )
    }

    val state: Flow<PaymentWholePeriodViewModelState> = _state.asFlow()

    fun setCharacterId(id: Long) {
        model.setCharacterId(id)
    }

    fun setCharacterId(id: Long?) = model.setCharacterId(id)

}
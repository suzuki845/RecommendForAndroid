package com.pin.recommend.ui.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.domain.model.PaymentTagListModel
import com.pin.recommend.domain.model.PaymentTagListModelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

data class PaymentTagListViewModelState(
    val modelState: PaymentTagListModelState = PaymentTagListModelState(),
    val isEditMode: Boolean = false,
) {
    val action = modelState.action
    val status = modelState.status
    val type = modelState.type
    val errorMessage = modelState.errorMessage
    val typedTags = modelState.typedTags
    val typedName = if (type == 0) "Payタグリスト" else "貯金タグリスト"
}

class PaymentTagListViewModel(application: Application) : AndroidViewModel(application) {

    private val model = PaymentTagListModel(application)

    private val isEditMode = MutableStateFlow(false)

    val state = model.state.combine(isEditMode) { state, isEditMode ->
        PaymentTagListViewModelState(state, isEditMode)
    }

    val subscribe: (LifecycleOwner) -> Unit = model::subscribe

    val setType: (Int) -> Unit = model::setType

    fun toggleEditMode() {
        isEditMode.value = !isEditMode.value
    }

    fun resetError() {
        model.resetError()
    }

    val insert: (PaymentTag) -> Unit = model::insert

    val update: (PaymentTag) -> Unit = model::update

    val delete: (PaymentTag) -> Unit = model::delete

}



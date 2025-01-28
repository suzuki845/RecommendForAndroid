package com.pin.recommend.ui.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.entity.PaymentAndTag
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.domain.model.PaymentEditor
import com.pin.recommend.domain.model.PaymentEditorState
import kotlinx.coroutines.flow.map
import java.util.Date


data class PaymentEditorViewModelState(
    private val modelState: PaymentEditorState = PaymentEditorState(),
) {
    val action = modelState.action
    val status = modelState.status
    val id = modelState.id
    val characterId = modelState.characterId
    val type = modelState.type
    val date = modelState.date
    val amount = modelState.amount
    val memo = modelState.memo
    val selectedTag = modelState.selectedTag
    val tags = modelState.tags
    val errorMessage = modelState.errorMessage
    val paymentTags = modelState.paymentTags
    val savingsTags = modelState.savingsTags
    val currentTags = modelState.currentTags
}

class PaymentEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val model = PaymentEditor(application)

    val state = model.state.map {
        PaymentEditorViewModelState(it)
    }

    val subscribe: (LifecycleOwner) -> Unit = model::subscribe

    val setId: (Long) -> Unit = model::setId

    val setEntityById: (Long) -> Unit = model::setEntityById

    val setEntity: (PaymentAndTag?) -> Unit = model::setEntity

    val setType: (Int) -> Unit = model::setType

    val setCharacterId: (Long) -> Unit = model::setCharacterId

    val setDate: (Date) -> Unit = model::setDate

    val setAmount: (Int) -> Unit = model::setAmount

    val setMemo: (String) -> Unit = model::setMemo

    val setTag: (PaymentTag?) -> Unit = model::setTag

    val resetError: () -> Unit = model::resetError

    val save: () -> Unit = model::save

}



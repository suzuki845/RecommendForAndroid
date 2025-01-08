package com.pin.recommend.ui.payment

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.R
import com.pin.recommend.domain.entity.PaymentAndTag
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.domain.model.PaymentEditor
import com.pin.recommend.domain.model.PaymentEditorState
import java.util.Date


data class PaymentEditViewModelState(
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

    fun payColor(context: Context) {
        if (modelState.type == 0) {
            ContextCompat.getColor(context, R.color.blue_600)
        } else {
            ContextCompat.getColor(context, R.color.grey_600)
        }
    }

    fun savingsColor(context: Context) {
        if (modelState.type != 0) {
            ContextCompat.getColor(context, R.color.blue_600)
        } else {
            ContextCompat.getColor(context, R.color.grey_600)
        }
    }
}

class PaymentEditViewModel(application: Application) : AndroidViewModel(application) {

    private val model = PaymentEditor(application)

    val state = model.state

    val subscribe: (LifecycleOwner) -> Unit = model::subscribe

    val setEntityById: (Long) -> Unit = model::setEntityById

    val setEntity: (PaymentAndTag?) -> Unit = model::setEntity

    val setType: (Int) -> Unit = model::setType

    val setCharacterId: (Long) -> Unit = model::setCharacterId

    val setDate: (Date) -> Unit = model::setDate

    val setAmount: (Int) -> Unit = model::setAmount

    val setMemo: (String) -> Unit = model::setMemo

    val setTag: (PaymentTag?) -> Unit = model::setTag

    val save: () -> Unit = model::save

}



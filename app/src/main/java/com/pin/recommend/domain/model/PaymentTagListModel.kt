package com.pin.recommend.domain.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.PaymentTagDao
import com.pin.recommend.domain.entity.PaymentTag
import kotlinx.coroutines.flow.MutableStateFlow


enum class PaymentTagListModelAction {
    Init,
    Insert,
    Update,
    Delete,
}

enum class PaymentTagListModelStatus {
    Processing,
    Success,
    Failure,
}

data class PaymentTagListModelState(
    val action: PaymentTagListModelAction? = PaymentTagListModelAction.Init,
    val status: PaymentTagListModelStatus = PaymentTagListModelStatus.Processing,
    val tags: List<PaymentTag> = emptyList(),
    val type: Int = 0,
    val errorMessage: String? = null
) {
    val typedTags: List<PaymentTag> = tags.filter { it.type == type }
}

class PaymentTagListModel(application: Application) : AndroidViewModel(application) {

    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val allTags = tagDao.watchAll()

    private val _state = MutableStateFlow(PaymentTagListModelState())

    val state = _state

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun setType(type: Int) {
        _state.value = _state.value.copy(type = type)
    }

    fun subscribe(owner: LifecycleOwner) {
        allTags.observe(owner) {
            _state.value = _state.value.copy(tags = it)
        }
    }

    fun insert(tag: PaymentTag) {
        try {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Insert,
                status = PaymentTagListModelStatus.Processing,
            )
            tagDao.insertPaymentTag(tag)
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Insert,
                status = PaymentTagListModelStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Insert,
                status = PaymentTagListModelStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun update(tag: PaymentTag) {
        try {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Update,
                status = PaymentTagListModelStatus.Processing,
            )
            tagDao.updatePaymentTag(tag)
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Update,
                status = PaymentTagListModelStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Update,
                status = PaymentTagListModelStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun delete(tag: PaymentTag) {
        try {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Delete,
                status = PaymentTagListModelStatus.Processing,
            )
            tagDao.deletePaymentTag(tag)
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Delete,
                status = PaymentTagListModelStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = PaymentTagListModelAction.Delete,
                status = PaymentTagListModelStatus.Failure,
                errorMessage = e.message
            )
        }
    }

}
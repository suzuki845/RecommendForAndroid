package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.PaymentDao
import com.pin.recommend.domain.dao.PaymentTagDao
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.entity.PaymentAndTag
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.util.TimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

data class PaymentEditorState(
    val action: PaymentEditorAction = PaymentEditorAction.Init,
    val status: PaymentEditorStatus = PaymentEditorStatus.Processing,
    val id: Long? = null,
    val characterId: Long? = null,
    val type: Int = 0,
    val date: Date = Date(),
    val amount: Int = 0,
    val memo: String = "",
    val selectedTag: PaymentTag? = null,
    val tags: List<PaymentTag> = listOf(),
    val errorMessage: String? = null
)

enum class PaymentEditorAction {
    Init,
    Save
}

enum class PaymentEditorStatus {
    Processing,
    Success,
    Failure,
}

class PaymentEditor(val context: Context) {
    private val paymentDao: PaymentDao = AppDatabase.getDatabase(context).paymentDao()
    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(context).paymentTagDao()

    private val _state = MutableStateFlow(PaymentEditorState())

    val state: StateFlow<PaymentEditorState> = _state

    fun subscribe(owner: LifecycleOwner) {
        tagDao.watchAll().observe(owner) {
            _state.value = _state.value.copy(tags = it)
        }
    }

    fun setEntityById(id: Long) {
        val e = paymentDao.findByIdPaymentAndTag(id)
        _state.value = PaymentEditorState(
            id = e?.payment?.id,
            characterId = e?.payment?.characterId,
            type = e?.payment?.type ?: 0,
            date = e?.payment?.createdAt ?: Date(),
            amount = e?.payment?.amount?.toInt() ?: 0,
            memo = e?.payment?.memo ?: "",
            selectedTag = e?.tag,
            tags = tagDao.findAll()
        )
    }

    fun setEntity(e: PaymentAndTag?) {
        _state.value = PaymentEditorState(
            id = e?.payment?.id,
            characterId = e?.payment?.characterId,
            type = e?.payment?.type ?: 0,
            date = e?.payment?.createdAt ?: Date(),
            amount = e?.payment?.amount?.toInt() ?: 0,
            memo = e?.payment?.memo ?: "",
            selectedTag = e?.tag,
            tags = tagDao.findAll()
        )
    }

    fun setCharacterId(characterId: Long) {
        _state.value = _state.value.copy(characterId = characterId)
    }

    fun setType(type: Int) {
        _state.value = _state.value.copy(type = type)
    }

    fun setDate(date: Date) {
        _state.value = _state.value.copy(date = date)
    }

    fun setAmount(amount: Int) {
        _state.value = _state.value.copy(amount = amount)
    }

    fun setMemo(memo: String) {
        _state.value = _state.value.copy(memo = memo)
    }

    fun setTag(tag: PaymentTag?) {
        _state.value = _state.value.copy(selectedTag = tag)
    }

    fun save() {
        try {
            val s = _state.value.copy(
                action = PaymentEditorAction.Save,
                status = PaymentEditorStatus.Processing
            )
            val characterId = s.characterId ?: throw Exception("character id is null.")
            val id = s.id ?: throw Exception("id is null.")
            val date = TimeUtil.resetDate(s.date)
            val amount = (s.amount).toDouble()
            val memo = s.memo
            val selectedTag = s.selectedTag
            val type = s.type
            val newPayment = Payment(
                id = id,
                characterId = characterId,
                type = type,
                amount = amount,
                memo = memo,
                paymentTagId = selectedTag?.id,
                createdAt = date,
                updatedAt = date
            )
            if (id == 0L) {
                paymentDao.insertPayment(newPayment)
                _state.value = _state.value.copy(id = newPayment.id)
            } else {
                paymentDao.updatePayment(newPayment)
            }

            _state.value = _state.value.copy(
                status = PaymentEditorStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                status = PaymentEditorStatus.Failure,
                errorMessage = e.message
            )
        }

    }

}





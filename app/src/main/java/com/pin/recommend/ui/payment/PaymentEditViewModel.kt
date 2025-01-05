package com.pin.recommend.ui.payment

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.pin.recommend.R
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.PaymentDao
import com.pin.recommend.domain.dao.PaymentTagDao
import com.pin.recommend.domain.entity.PaymentAndTag
import kotlinx.coroutines.launch

class PaymentEditViewModel(application: Application) : AndroidViewModel(application) {

    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val _tags = tagDao.findTrackedAll()

    fun load(id: Long) {
        viewModelScope.launch {
            paymentAndTag.value = paymentDao.findByIdPaymentAndTag(id)
        }
    }

    val paymentAndTag = MutableLiveData<PaymentAndTag>()

    val tags
        get() = paymentAndTag.switchMap { paymentAndTag ->
            _tags.map { paymentTags ->
                paymentTags.filter {
                    it.type == paymentAndTag.payment.type
                }
            }
        }


    val payColor = paymentAndTag.map {
        if (it.payment.type == 0) {
            ContextCompat.getColor(application, R.color.blue_600)
        } else {
            ContextCompat.getColor(application, R.color.grey_600)
        }
    }

    val savingsColor = paymentAndTag.map {
        if (it.payment.type != 0) {
            ContextCompat.getColor(application, R.color.blue_600)
        } else {
            ContextCompat.getColor(application, R.color.grey_600)
        }
    }

    val selectedTag = paymentAndTag.switchMap { current ->
        tags.map { list ->
            list.find { tag ->
                current.tag?.id.let { id -> id == tag.id }
            }
        }
    }


    fun updatePayment(): Boolean {

        //Log.d("UPDATE!! amount", paymentAndTag.value?.payment?.amount.toString())
        //Log.d("UPDATE!! updatedAt", paymentAndTag.value?.payment?.updatedAt.toString())
        //Log.d("UPDATE!! memo", paymentAndTag.value?.payment?.memo.toString())

        val paymentAndTag = paymentAndTag.value
        val payment = paymentAndTag?.payment
        //val date = TimeUtil.resetDate( Date())
        //payment?.updatedAt = date
        payment?.paymentTagId = selectedTag.value?.id
        return payment?.let { paymentDao.updatePayment(it) } != 0
    }


}
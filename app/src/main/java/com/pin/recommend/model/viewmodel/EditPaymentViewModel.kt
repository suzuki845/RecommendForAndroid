package com.pin.recommend.model.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.pin.recommend.R
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.entity.PaymentAndTag
import com.pin.recommend.util.TimeUtil
import kotlinx.coroutines.launch
import java.util.*

class EditPaymentViewModel(application: Application) : AndroidViewModel(application)  {

    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val _tags = tagDao.findTrackedAll()

    fun load(id: Long){
        viewModelScope.launch {
            paymentAndTag.value = paymentDao.findByIdPaymentAndTag(id)
        }
    }

    val paymentAndTag = MutableLiveData<PaymentAndTag>()

    val tags
        get() = Transformations.switchMap(paymentAndTag) {paymentAndTag ->
            _tags.map { paymentTags ->
                paymentTags.filter {
                    it.type == paymentAndTag.payment.type
                }
            }
        }


    val payColor = paymentAndTag.map {
        if (it.payment.type == 0) {
            ContextCompat.getColor(application, R.color.blue_600)
        }else{
            ContextCompat.getColor(application, R.color.grey_600)
        }
    }

    val savingsColor  = paymentAndTag.map {
        if (it.payment.type != 0) {
            ContextCompat.getColor(application, R.color.blue_600)
        }else{
            ContextCompat.getColor(application, R.color.grey_600)
        }
    }

    val selectedTag = paymentAndTag.switchMap{ current ->
            tags.map { list ->
                list.find {tag ->
                    current.tag?.id.let { id -> id ==  tag.id}
                }
            }
        }


    fun updatePayment(): Boolean{

        Log.d("UPDATE!! amount", paymentAndTag.value?.payment?.amount.toString())
        Log.d("UPDATE!! updatedAt", paymentAndTag.value?.payment?.updatedAt.toString())
        Log.d("UPDATE!! memo", paymentAndTag.value?.payment?.memo.toString())

        val paymentAndTag = paymentAndTag.value
        val payment = paymentAndTag?.payment
        payment?.paymentTagId = selectedTag.value?.id
        return paymentDao.updatePayment(payment) != 0
    }


}
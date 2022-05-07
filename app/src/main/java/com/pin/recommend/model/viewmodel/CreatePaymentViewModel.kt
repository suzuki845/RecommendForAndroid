package com.pin.recommend.model.viewmodel

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.pin.recommend.R
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.PaymentTag
import com.pin.recommend.util.TimeUtil
import kotlinx.coroutines.launch
import java.util.*

class CreatePaymentViewModel(application: Application) : AndroidViewModel(application)  {

    private val characterDao: RecommendCharacterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val _tags = tagDao.findTrackedAll()

    val tags
        get() = switchMap(type) { payType ->
            _tags.map { paymentTags ->
                paymentTags.filter {
                    it.type == payType
                }
            }
        }

    val type = MutableLiveData(0)

    val payColor = type.map {
            if (it == 0) {
                ContextCompat.getColor(application, R.color.blue_600)
            }else{
                ContextCompat.getColor(application, R.color.grey_600)
            }
        }

    val savingsColor  = type.map {
        if (it != 0) {
            ContextCompat.getColor(application, R.color.blue_600)
        }else{
            ContextCompat.getColor(application, R.color.grey_600)
        }
    }

    val characterId = MutableLiveData<Long?>()

    val date = MutableLiveData<Date>(Date())

    val amount = MutableLiveData<Int>(0)

    val memo = MutableLiveData<String>("")

    val tag = MutableLiveData<PaymentTag?>()

    val selectedTag = tags.switchMap{ list ->
            tag.map { selected ->
                list.find {
                    selected?.id.let { id -> id ==  it.id}
                }
            }
        }


    fun createPayment(): Boolean{
        val characterId = characterId.value ?: return false
        val date = TimeUtil.resetDate(date.value ?: Date())
        //print("test!! $date")
        val amount = (amount.value ?: 0).toDouble()
        val memo  = memo.value
        val selectedTag = selectedTag.value
        val type = type.value ?: 0
        val newPayment = Payment(
                id = 0,
                characterId = characterId,
                type = type,
                amount = amount,
                memo = memo,
                paymentTagId = selectedTag?.id,
                createdAt = date,
                updatedAt = date
        )
        val r = paymentDao.insertPayment(newPayment) != 0L
        return r;
    }


}



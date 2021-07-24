package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.pin.recommend.R
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.PaymentTag
import kotlinx.coroutines.launch
import java.util.*

class PaymentTagViewModel(application: Application) : AndroidViewModel(application)  {

    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val _tags = tagDao.findTrackedAll()

    private val allTags = _tags

    val currentTags
        get() = Transformations.switchMap(type) { payType ->
            _tags.map { paymentTags ->
                paymentTags.filter {
                    it.type == payType
                }
            }
        }

    val isEditMode = MutableLiveData(false)

    val type = MutableLiveData(0)

    fun insertTag(tag: PaymentTag) = viewModelScope.launch{
        tagDao.insertPaymentTag(tag)
    }

    fun updateTag(tag: PaymentTag) = viewModelScope.launch {
        tagDao.updatePaymentTag(tag)
    }

    fun deleteTag(tag: PaymentTag) = viewModelScope.launch {
        tagDao.deletePaymentTag(tag)
    }

}



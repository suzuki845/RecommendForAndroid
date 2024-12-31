package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.entity.PaymentTag
import kotlinx.coroutines.launch

class PaymentTagViewModel(application: Application) : AndroidViewModel(application) {

    private val tagDao: PaymentTagDao = AppDatabase.getDatabase(application).paymentTagDao()

    private val _tags = tagDao.findTrackedAll()

    private val allTags = _tags

    val currentTags
        get() = type.switchMap { payType ->
            _tags.map { paymentTags ->
                paymentTags.filter {
                    it.type == payType
                }
            }
        }

    val isEditMode = MutableLiveData(false)

    val type = MutableLiveData(0)

    fun insertTag(tag: PaymentTag) = viewModelScope.launch {
        tagDao.insertPaymentTag(tag)
    }

    fun updateTag(tag: PaymentTag) = viewModelScope.launch {
        tagDao.updatePaymentTag(tag)
    }

    fun deleteTag(tag: PaymentTag) = viewModelScope.launch {
        tagDao.deletePaymentTag(tag)
    }

}



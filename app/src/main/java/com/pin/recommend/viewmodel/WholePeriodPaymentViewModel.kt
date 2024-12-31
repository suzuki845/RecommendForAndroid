package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.WholePeriodCharacterPaymentModel
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.RecommendCharacterDao

class WholePeriodPaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDao: RecommendCharacterDao =
        AppDatabase.getDatabase(application).recommendCharacterDao()
    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val paymentModel by lazy {
        WholePeriodCharacterPaymentModel(paymentDao, characterDao)
    }

    val isEditMode = MutableLiveData(false)

    val characterId = paymentModel.characterId

    val character = paymentModel.character

    fun setCharacterId(id: Long?) = paymentModel.setCharacterId(id)

    val wholePeriodPaymentAmount = paymentModel.wholePeriodPaymentAmount

    val wholePeriodSavingsAmount = paymentModel.wholePeriodSavingsAmount

}
package com.pin.recommend.model.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.pin.recommend.MyApplication
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterMonthlyPaymentModel
import com.pin.recommend.model.WholePeriodCharacterPaymentModel
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.util.TimeUtil
import kotlinx.coroutines.launch
import java.util.*

class WholePeriodPaymentViewModel (application: Application) : AndroidViewModel(application) {

    private val characterDao: RecommendCharacterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
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
package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterMonthlyPaymentModel
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Payment
import kotlinx.coroutines.launch
import java.util.Date

class PaymentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val characterDao: RecommendCharacterDao =
        AppDatabase.getDatabase(application).recommendCharacterDao()
    private val monthlyPaymentModel by lazy {
        CharacterMonthlyPaymentModel(paymentDao, characterDao)
    }

    val isEditMode = MutableLiveData(false)

    val characterId = monthlyPaymentModel.characterId

    val currentDate = monthlyPaymentModel.currentDate

    val monthlyPayment = monthlyPaymentModel.monthlyPayment

    val monthlyPaymentAmount = monthlyPaymentModel.monthlyPaymentAmount

    val monthlySavingsAmount = monthlyPaymentModel.monthlySavingsAmount

    fun nextMonth() = monthlyPaymentModel.nextMonth()

    fun prevMonth() = monthlyPaymentModel.prevMonth()

    fun setCharacterId(id: Long?) = monthlyPaymentModel.setCharacterId(id)

    fun setCurrentDate(date: Date) = monthlyPaymentModel.setCurrentDate(date)

    fun deletePayment(payment: Payment) = viewModelScope.launch {
        paymentDao.deletePayment(payment)
    }

    fun updatePayment(payment: Payment) = viewModelScope.launch {
        paymentDao.updatePayment(payment)
    }


}



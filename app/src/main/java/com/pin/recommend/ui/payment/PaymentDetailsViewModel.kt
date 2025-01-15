package com.pin.recommend.ui.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.PaymentDao
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.model.MonthlyPaymentModel
import kotlinx.coroutines.launch
import java.util.Date

class PaymentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val paymentDao: PaymentDao = AppDatabase.getDatabase(application).paymentDao()
    private val monthlyPaymentModel by lazy {
        MonthlyPaymentModel(application)
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


}



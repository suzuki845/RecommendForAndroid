package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.PaymentDao
import com.pin.recommend.domain.dao.RecommendCharacterDao
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.entity.PaymentAndTag
import com.pin.recommend.util.TimeUtil
import java.util.Calendar
import java.util.Date

class PaymentBetweenCharacterDatesModel(
    private val paymentDao: PaymentDao,
    private val characterDao: RecommendCharacterDao
) {

    val characterId = MutableLiveData<Long?>()

    val character = characterId.switchMap {
        characterDao.watchById(it ?: -1)
    }

    val section = MutableLiveData<Section>(Section(Date(), Date()))

    val groupingByDate = characterId.switchMap { characterId ->
        section.switchMap { dates ->
            paymentDao.findByTrackedCharacterIdPaymentAndTagInDate(
                characterId ?: -1,
                dates.startDate,
                dates.endDate
            ).map {
                it.groupBy {
                    it.payment.createdAt
                }
            }
        }
    }

    val groupingByDateWholePeriod = characterId.switchMap { characterId ->
        section.switchMap { dates ->
            paymentDao.findByTrackedCharacterIdPaymentAndTag(characterId ?: -1).map {
                it.groupBy {
                    it.payment.createdAt
                }
            }
        }
    }

    fun delete(payment: Payment) {
        paymentDao.deletePayment(payment)
    }

    companion object
    class Section(
        val startDate: Date,
        val endDate: Date
    )

}

class MonthlyPaymentModel(
    context: Context
) {

    private val db = AppDatabase.getDatabase(context)
    private val paymentDao = db.paymentDao()
    private val characterDao = db.recommendCharacterDao()

    private val paymentBetweenCharacterDatesModel by lazy {
        PaymentBetweenCharacterDatesModel(paymentDao, characterDao)
    }

    val characterId: LiveData<Long?> = paymentBetweenCharacterDatesModel.characterId

    val character = paymentBetweenCharacterDatesModel.character

    fun setCharacterId(id: Long?) {
        paymentBetweenCharacterDatesModel.characterId.value = id
    }

    val currentDate: LiveData<Date> get() = _currentDate
    private val _currentDate = MutableLiveData(Date())

    fun setCurrentDate(date: Date) {
        _currentDate.value = date
        val startDay = TimeUtil.monthlyStartDate(date)
        val endDay = TimeUtil.monthlyEndDate(date)
        val section = PaymentBetweenCharacterDatesModel.Section(startDay, endDay)
        paymentBetweenCharacterDatesModel.section.value = section
    }

    fun nextMonth() {
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, 1)
        setCurrentDate(calendar.time)
    }

    fun prevMonth() {
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, -1)
        setCurrentDate(calendar.time)
    }

    fun delete(payment: Payment) {
        paymentBetweenCharacterDatesModel.delete(payment)
    }

    val monthlyPayment = paymentBetweenCharacterDatesModel.groupingByDate.map { MonthlyPayment(it) }

    val monthlyPaymentAmount = paymentBetweenCharacterDatesModel.groupingByDate.map { dates ->
        var amount = 0.0
        dates.values.forEach { paymentAndTags ->
            paymentAndTags.forEach { paymentAndTag ->
                if (paymentAndTag.payment.type == 0) {
                    amount += paymentAndTag.payment.amount
                }
            }
        }
        return@map amount.toInt()
    }

    val monthlySavingsAmount = paymentBetweenCharacterDatesModel.groupingByDate.map { dates ->
        var amount = 0.0
        dates.values.forEach { paymentAndTags ->
            paymentAndTags.forEach { paymentAndTag ->
                if (paymentAndTag.payment.type == 1) {
                    amount += paymentAndTag.payment.amount
                }
            }
        }
        return@map amount.toInt()
    }

}


class WholePeriodCharacterPaymentModel(
    private val paymentDao: PaymentDao,
    private val characterDao: RecommendCharacterDao
) {

    private val paymentBetweenCharacterDatesModel by lazy {
        PaymentBetweenCharacterDatesModel(paymentDao, characterDao)
    }

    val characterId: LiveData<Long?> = paymentBetweenCharacterDatesModel.characterId

    val character = paymentBetweenCharacterDatesModel.character

    fun setCharacterId(id: Long?) {
        paymentBetweenCharacterDatesModel.characterId.value = id
    }

    val groupingByDateWholePeriod = paymentBetweenCharacterDatesModel.groupingByDateWholePeriod

    val wholePeriodPaymentAmount = groupingByDateWholePeriod.map { dates ->
        var amount = 0.0
        dates.values.forEach { paymentAndTags ->
            paymentAndTags.forEach { paymentAndTag ->
                if (paymentAndTag.payment.type == 0) {
                    amount += paymentAndTag.payment.amount
                }
            }
        }
        return@map amount.toInt()
    }

    val wholePeriodSavingsAmount = groupingByDateWholePeriod.map { dates ->
        var amount = 0.0
        dates.values.forEach { paymentAndTags ->
            paymentAndTags.forEach { paymentAndTag ->
                if (paymentAndTag.payment.type == 1) {
                    amount += paymentAndTag.payment.amount
                }
            }
        }
        return@map amount.toInt()
    }


}


data class MonthlyPayment(
    val payments: Map<Date, List<PaymentAndTag>> = mapOf()
) {

    val totalPayment
        get() = payments.values
            .flatten()
            .map { it.payment }
            .filter { it.type == 0 }
            .sumOf { it.amount }.toInt()

    val totalSavings
        get() = payments.values
            .flatten()
            .map { it.payment }
            .filter { it.type == 1 }
            .sumOf { it.amount }.toInt()

}


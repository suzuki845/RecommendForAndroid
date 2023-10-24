package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterMonthlyEventModel
import com.pin.recommend.model.CharacterMonthlyPaymentModel
import com.pin.recommend.model.dao.EventDao
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.RecommendCharacter
import kotlinx.coroutines.launch
import java.util.*

class EventDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao: EventDao = AppDatabase.getDatabase(application).eventDao()
    private val characterDao: RecommendCharacterDao =
        AppDatabase.getDatabase(application).recommendCharacterDao()
    private val monthlyEventModel by lazy {
        CharacterMonthlyEventModel(eventDao, characterDao)
    }

    val isEditMode = MutableLiveData(false)

    val character = monthlyEventModel.character

    val currentDate = monthlyEventModel.currentDate

    fun nextMonth() = monthlyEventModel.nextMonth()

    fun prevMonth() = monthlyEventModel.prevMonth()

    fun setCharacter(character: RecommendCharacter) = monthlyEventModel.setCharacter(character)

    fun setCharacter(id: Long) = monthlyEventModel.setCharacter(id)


    fun setCurrentDate(date: Date) = monthlyEventModel.setCurrentDate(date)

    fun deleteEvent(event: Event) = viewModelScope.launch {
        eventDao.deleteEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        eventDao.updateEvent(event)
    }

    val monthlyEvent = monthlyEventModel.monthlyEvent

    val selectedMonthlyEvent = monthlyEventModel.selectedMonthlyEvent

}


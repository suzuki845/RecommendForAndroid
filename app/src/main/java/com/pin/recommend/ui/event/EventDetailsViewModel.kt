package com.pin.recommend.ui.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.EventDao
import com.pin.recommend.domain.dao.RecommendCharacterDao
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.entity.RecommendCharacter
import com.pin.recommend.domain.model.CharacterMonthlyEventModel
import kotlinx.coroutines.launch
import java.util.Date

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


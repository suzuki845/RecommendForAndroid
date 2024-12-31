package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.EventDao
import com.pin.recommend.model.entity.Event
import kotlinx.coroutines.launch
import java.util.Date

class EditEventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao: EventDao = AppDatabase.getDatabase(application).eventDao()

    fun load(id: Long) {
        viewModelScope.launch {
            val event = eventDao.findById(id) ?: throw RuntimeException("missing event $id")
            target.value = event
            date.value = event.date
            title.value = event.title ?: ""
            memo.value = event.memo ?: ""
        }
    }

    val target = MutableLiveData<Event>()

    val date = MutableLiveData<Date>()

    val title = MutableLiveData<String>("")

    val memo = MutableLiveData<String>("")

    fun updateEvent(): Boolean {
        val event = target.value
        event?.title = title.value
        event?.memo = memo.value
        event?.date = date.value ?: throw RuntimeException("save failed date value is null")

        return event?.let { eventDao.updateEvent(it) } != 0
    }


}

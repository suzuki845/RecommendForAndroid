package com.pin.recommend.model.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.pin.recommend.R
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.EventDao
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.PaymentTag
import com.pin.recommend.util.TimeUtil
import java.util.*

class CreateEventViewModel(application: Application) : AndroidViewModel(application)  {

    private val characterDao: RecommendCharacterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
    private val eventDao: EventDao = AppDatabase.getDatabase(application).eventDao()

    val characterId = MutableLiveData<Long?>()

    val date = MutableLiveData<Date>(Date())

    val title = MutableLiveData<String>("")

    val memo = MutableLiveData<String>("")

    fun createEvent(): Boolean{
        val characterId = characterId.value ?: return false
        val date = TimeUtil.resetDate(date.value ?: Date())
        val title = title.value
        val memo  = memo.value
        val newEvent = Event(
                id = 0,
                characterId = characterId,
                title = title,
                memo = memo,
                date = date,
        )
        //Log.d("DateIn create", date.toString())
        return eventDao.insertEvent(newEvent) != 0L
    }


}



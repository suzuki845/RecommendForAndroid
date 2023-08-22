package com.pin.recommend.model

import android.util.Log
import androidx.lifecycle.*
import com.pin.recommend.model.dao.EventDao
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.value.Month
import com.pin.recommend.model.value.Section
import com.pin.recommend.util.TimeUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Observer

class EventBetweenDatesModel(private val eventDao: EventDao, private val characterDao: RecommendCharacterDao) {

    val characterId = MutableLiveData<Long?>()

    val character = characterId.switchMap {
        characterDao.watchById(it ?: -1)
    }

    val section = MutableLiveData<Section<Date>>(Section(Date(), Date()))

    val groupingByDate = characterId.switchMap { characterId ->
        section.switchMap { dates ->
            eventDao.findByTrackedCharacterIdEventInDate(characterId ?: -1, dates.startDate, dates.endDate).map {
                it.groupBy {
                     TimeUtil.resetDate(it.date)
                }
            }
        }
    }

}


class CharacterMonthlyEventModel(private val eventDao: EventDao, private val characterDao: RecommendCharacterDao) {

    private val eventBetweenDatesModel by lazy {
        EventBetweenDatesModel(eventDao, characterDao)
    }

    val character = eventBetweenDatesModel.character
    fun setCharacter(character: RecommendCharacter){
        eventBetweenDatesModel.characterId.value = character.id
    }

    private var _currentDate: MutableLiveData<Date>  = MutableLiveData(Date())
    val currentDate: LiveData<Date> = _currentDate

    fun setCurrentDate(date: Date){
        _currentDate.value = date
        val startDay = TimeUtil.monthlyStartDate(date)
        val endDay  = TimeUtil.monthlyEndDate(date)
        //val s = SimpleDateFormat("yyyy/MM/dd");
        //Log.d("dateSelected", "current => ${s.format(date)} => ${s.format(startDay)} ~ ${s.format(endDay)}")
        val section = Section<Date>(startDay, endDay)
        eventBetweenDatesModel.section.value = section
    }

    fun nextMonth(){
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, 1)
        setCurrentDate(calendar.time)
    }

    fun prevMonth(){
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, -1)
        setCurrentDate(calendar.time)
    }

    val monthInDays = currentDate.map {
        val startDate = TimeUtil.monthlyStartDate(it)
        val endDate  = TimeUtil.monthlyEndDate(it)

        var result = listOf<Date>()
        val calendar = Calendar.getInstance()
        var next = TimeUtil.resetDate(startDate)
        while (next <= endDate){
            calendar.time = next
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            result += next
            next = calendar.time
        }
        result
    }

    val events = eventBetweenDatesModel.groupingByDate

    val monthlyEvent = Combine2LiveData(monthInDays, events){days, events ->
        MonthlyEvent(days ?: listOf(), events ?: mapOf())
    }

    val selectedMonthlyEvent = Combine2LiveData(currentDate, monthlyEvent){date, events ->
        SelectedMonthlyEvent(date ?: Date(), events ?: MonthlyEvent( listOf(), mapOf()))
    }

}

class SelectedMonthlyEvent(
        val selectedDate: Date,
        val monthlyEvent: MonthlyEvent
){}

class MonthlyEvent(
        val days: List<Date>,
        val events: Map<Date, List<Event>>
) {
    fun dayHasEvents(date: Date): Boolean{
        return events[date] != null
    }
}

class Combine2LiveData<T, K, S>(source1: LiveData<T>, source2: LiveData<K>, private val combine: (data1: T?, data2: K?) -> S) : MediatorLiveData<S>() {

    private var data1: T? = null
    private var data2: K? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2)
        }
    }

    override fun <T : Any?> removeSource(toRemove: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

class Combine3LiveData<T1, T2, T3, S>(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>, private val combine: (data1: T1?, data2: T2?, data3: T3?) -> S) : MediatorLiveData<S>() {

    private var data1: T1? = null
    private var data2: T2? = null
    private var data3: T3? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2, data3)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2, data3)
        }
        super.addSource(source3) {
            data3 = it
            value = combine(data1, data2, data3)
        }
    }

    override fun <T : Any?> removeSource(toRemove: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

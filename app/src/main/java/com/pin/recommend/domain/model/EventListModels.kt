package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.dao.EventDao
import com.pin.recommend.domain.dao.RecommendCharacterDao
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.entity.RecommendCharacter
import com.pin.recommend.domain.value.Section
import com.pin.recommend.util.TimeUtil
import com.pin.recommend.util.combine2
import java.util.Calendar
import java.util.Date


class EventBetweenDatesModel(
    private val eventDao: EventDao,
    private val characterDao: RecommendCharacterDao
) {

    val characterId = MutableLiveData<Long?>()

    val character = characterId.switchMap {
        characterDao.watchById(it ?: -1)
    }

    val section = MutableLiveData<Section<Date>>(Section(Date(), Date()))

    val groupingByDate = characterId.switchMap { characterId ->
        section.switchMap { dates ->
            eventDao.watchByCharacterIdEventInDate(
                characterId ?: -1,
                dates.startDate,
                dates.endDate
            ).map {
                it.groupBy {
                    TimeUtil.resetDate(it.date)
                }
            }
        }
    }

    fun delete(event: Event) {
        eventDao.deleteEvent(event)
    }

}


class CharacterMonthlyEventModel(
    context: Context
) {

    private val db = AppDatabase.getDatabase(context)
    private val eventDao = db.eventDao()
    private val characterDao = db.recommendCharacterDao()

    private val eventBetweenDatesModel by lazy {
        EventBetweenDatesModel(eventDao, characterDao)
    }

    val character = eventBetweenDatesModel.character
    fun setCharacter(character: RecommendCharacter) {
        eventBetweenDatesModel.characterId.value = character.id
    }

    fun setCharacter(id: Long) {
        eventBetweenDatesModel.characterId.value = id
    }

    private var _currentDate: MutableLiveData<Date> = MutableLiveData(Date())
    val currentDate: LiveData<Date> = _currentDate

    fun setCurrentDate(date: Date) {
        _currentDate.value = date
        val startDay = TimeUtil.monthlyStartDate(date)
        val endDay = TimeUtil.monthlyEndDate(date)
        //val s = SimpleDateFormat("yyyy/MM/dd");
        //Log.d("dateSelected", "current => ${s.format(date)} => ${s.format(startDay)} ~ ${s.format(endDay)}")
        val section = Section<Date>(startDay, endDay)
        eventBetweenDatesModel.section.value = section
    }

    fun nextMonth() {
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        if (current != null) {
            calendar.time = current
        }
        calendar.add(Calendar.MONTH, 1)
        setCurrentDate(calendar.time)
    }

    fun prevMonth() {
        val current = _currentDate.value
        val calendar = Calendar.getInstance()
        if (current != null) {
            calendar.time = current
        }
        calendar.add(Calendar.MONTH, -1)
        setCurrentDate(calendar.time)
    }

    private val monthInDays = currentDate.map {
        val startDate = TimeUtil.monthlyStartDate(it)
        val endDate = TimeUtil.monthlyEndDate(it)

        var result = listOf<Date>()
        val calendar = Calendar.getInstance()
        var next = TimeUtil.resetDate(startDate)
        while (next <= endDate) {
            calendar.time = next
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            result += next
            next = calendar.time
        }
        result
    }

    private val events = eventBetweenDatesModel.groupingByDate

    private val monthlyEvent = combine2(monthInDays, events) { days, events ->
        val filled = (days ?: listOf()).map { day ->
            val map = (events ?: mapOf())
            DateWithEvents(day, map[day] ?: listOf())
        }
        MonthlyEvent(filled)
    }

    val selectedMonthlyEvent = combine2(currentDate, monthlyEvent) { date, events ->
        SelectedMonthlyEvent(
            date ?: Date(),
            events ?: MonthlyEvent(
                listOf()
            )
        )
    }

    fun delete(event: Event) {
        eventBetweenDatesModel.delete(event)
    }

}

class SelectedMonthlyEvent(
    val selectedDate: Date = Date(),
    val monthlyData: MonthlyEvent = MonthlyEvent()
) {
    fun indexOfSelectedDate(): Int? {
        val index = monthlyData.indexOf(TimeUtil.resetDate(selectedDate))
        if (index == -1) {
            return null
        }
        return index
    }
}

class MonthlyEvent(
    val result: List<DateWithEvents> = listOf()
) {
    val days = result.map { it.date }

    fun dayHasEvents(date: Date): Boolean {
        return result.find { it.date == date }?.hasEvents ?: true
    }

    fun indexOf(date: Date): Int {
        return days.indexOfFirst { it == date }
    }
}

data class DateWithEvents(val date: Date, val events: List<Event>) {
    val hasEvents = events.isNotEmpty()
}

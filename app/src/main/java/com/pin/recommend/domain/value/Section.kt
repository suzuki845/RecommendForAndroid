package com.pin.recommend.domain.value

import com.pin.recommend.util.TimeUtil
import java.util.*

class Section<T>(
        val startDate: Date,
        val endDate: Date
)

class Month(
        val current: Date,
){
    val section: Section<Date>
    init{
        val startDay = TimeUtil.monthlyStartDate(current)
        val endDay  = TimeUtil.monthlyEndDate(current)
        section = Section(startDay, endDay)
    }

    fun nextMonth(): Month{
        val current = current
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, 1)
        return Month(calendar.time)
    }

    fun prevMonth(){
        val current = current
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.MONTH, -1)
    }



}
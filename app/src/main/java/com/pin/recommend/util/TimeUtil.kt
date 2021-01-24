package com.pin.recommend.util

import androidx.room.Ignore
import java.util.*

class TimeUtil {
    companion object {
        private val MILLIS_OF_DAY = 1000 * 60 * 60 * 24
        @JvmStatic
        fun getDiffDays(calendar1: Calendar, calendar2: Calendar): Long {
            val diffTime = calendar1.timeInMillis - calendar2.timeInMillis
            return diffTime / MILLIS_OF_DAY
        }

        @JvmStatic
        fun resetTime(calendar: Calendar) {
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            // 時の部分をクリアするには、setで入れないといけない。
            calendar[Calendar.HOUR_OF_DAY] = 0
        }
    }
}

package com.pin.recommend.util

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

        @JvmStatic
        fun resetDate(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            // 時の部分をクリアするには、setで入れないといけない。
            calendar[Calendar.HOUR_OF_DAY] = 0
            return calendar.time
        }

        @JvmStatic
        fun monthlyStartDate(currentDate: Date): Date {
            val cal = Calendar.getInstance()

            //年月をセットする
            cal.time = currentDate
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            var day = 1
            cal[year, month, day, 0, 0] = 0

            return cal.time
        }

        @JvmStatic
        fun monthlyEndDate(currentDate: Date): Date {
            val cal = Calendar.getInstance()

            //年月をセットする
            cal.time = currentDate
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
                //月末日を取得する
            val day = cal.getActualMaximum(Calendar.DATE)
            cal[year, month, day, 0, 0] = 0
            return cal.time
        }


    }
}

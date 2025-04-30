package com.self.lovenotes.data.util

import java.time.YearMonth

object utils {
    fun getMonthRange(yearMonth: YearMonth): Pair<String, String> {
        val year = yearMonth.year
        val month = String.format("%02d",yearMonth.monthValue)
        val daysInMonth = yearMonth.lengthOfMonth()
        return ("$year-$month-01" to "$year-$month-$daysInMonth")
    }
}
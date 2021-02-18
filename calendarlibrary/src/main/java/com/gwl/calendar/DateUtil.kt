package com.gwl.calendar

import java.util.Calendar
import java.util.Date

object DateUtil {

    fun getCanlenderFordate(scheduleDate: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = scheduleDate
        return cal
    }
}
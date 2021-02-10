package com.gwl.shweta.calendarlibrary

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gwl.shweta.calendar.CalendarView
import com.gwl.shweta.calendar.CalendarView.CalendarBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        CalendarBuilder(this, calendarView)
            .setDatesColor(ContextCompat.getColor(this,R.color.dark_gray))
            .setDaysColor(ContextCompat.getColor(this,R.color.blue))
            .setDisabledDatesColor(ContextCompat.getColor(this,R.color.light_gray))
            .setMonthHeaderColor(ContextCompat.getColor(this,R.color.blue))
            .setSelectedDatesColor(ContextCompat.getColor(this,R.color.white))
            .build()
        calendarView.isDisableNextfutureDate(true)
        calendarView.updateCalendar()
        findViewById<Button>(R.id.search_close_btn).setOnClickListener(View.OnClickListener {
            Log.i("Selected Dates", "onCreate: "+calendarView.getSelectedDates())

        })
//        calendarView?.also { it.buil }
    }
}
package com.gwl.shweta.calendarlibrary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gwl.calendar.CalendarView
import com.gwl.calendar.CalendarView.CalendarConfig
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        /*CalendarConfig(this, calendarView)
            .setDatesColor(ContextCompat.getColor(this, R.color.dark_gray))
            .setDaysColor(ContextCompat.getColor(this, R.color.blue))
            .setDisabledDatesColor(ContextCompat.getColor(this, R.color.light_gray))
            .setMonthHeaderColor(ContextCompat.getColor(this, R.color.blue))
            .setSelectedDatesColor(ContextCompat.getColor(this, R.color.white))
//            .setCurrentDateBackgroundImage(ContextCompat.getDrawable(this, R.drawable.ic_redright_arrow))
//            .setEventImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle_red_dot))
            .setEventIconPosition(CalendarConfig.EventIconPosition.TOP_RIGHT)
            .build()*/
        calendarView.isDisableNextfutureDate(true)
        var date = mutableListOf<Date>()
        date.add(Calendar.getInstance().time)
        calendarView.setEvents(date)
        calendarView.updateCalendar()
        findViewById<Button>(R.id.search_close_btn).setOnClickListener(View.OnClickListener {
            Log.i("Selected Dates", "onCreate: " + calendarView.getSelectedDates())
        })
    }
}
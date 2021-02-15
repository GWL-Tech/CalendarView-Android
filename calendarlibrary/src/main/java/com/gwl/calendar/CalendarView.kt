package com.gwl.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarView : LinearLayout {

    // region - Private Properties
    // date format
    private var dateFormat: String? = null
    private var monthColor: Int = 0
    private var colorDays: Int = 0
    private var colorDates: Int = 0
    private var disableColorDates: Int = 0
    private var selectedColorDates: Int = 0
    private var currentDateBackgroundImgDrawable: Drawable? = null
    private var selectedDates: ArrayList<Date> = ArrayList()

    // current displayed month
    private val currentDate = Calendar.getInstance()

    //event handling
    private var eventHandler: EventHandler? = null

    //event list
    private var eventsList: List<Date> = mutableListOf()

    //disable future dates
    private var isDisableNextMonthDate: Boolean = false

    //xml component
    private var header: LinearLayout? = null
    private var btnPrev: ImageView? = null
    private var btnNext: ImageView? = null
    private var txtDate: TextView? = null
    private var grid: GridView? = null
    private var calendarAdapter: CalendarAdapter? = null
    // endregion

    // region - constructor
    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadDateFormat(attrs)
        initControl(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        loadDateFormat(attrs)
        initControl(context)
    }
    // endregion


    /**
     * Load control xml layout
     */
    private fun initControl(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.control_calendar, this)
        //loadDateFormat(attrs)
        assignUiElements()
        assignClickHandlers()
    }

    /**
     * Load date format
     */
    private fun loadDateFormat(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        try {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat)
            monthColor = ta.getInt(
                R.styleable.CalendarView_colorMonth,
                ContextCompat.getColor(context, R.color.white)
            )
            colorDays = ta.getInt(
                R.styleable.CalendarView_colorDays,
                ContextCompat.getColor(context, R.color.white)
            )
            colorDates = ta.getInt(
                R.styleable.CalendarView_colorDates,
                ContextCompat.getColor(context, R.color.white)
            )
            disableColorDates = ta.getInt(
                R.styleable.CalendarView_disableColorDates,
                ContextCompat.getColor(context, R.color.white)
            )
            selectedColorDates = ta.getInt(
                R.styleable.CalendarView_selectedColorDates,
                ContextCompat.getColor(context, R.color.white)
            )
            currentDateBackgroundImgDrawable = ta.getDrawable(R.styleable.CalendarView_currentDateBackgroundImg)
            if (dateFormat == null) dateFormat = DATE_FORMAT
            if (currentDateBackgroundImgDrawable == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    currentDateBackgroundImgDrawable = resources.getDrawable(R.drawable.reminder, null)
                } else {
                    currentDateBackgroundImgDrawable = resources.getDrawable(R.drawable.reminder)
                }
            }
        } finally {
            ta.recycle()
        }
    }

    /**
     * Load ui elements
     */
    private fun assignUiElements() {
        // layout is inflated, assign local variables to components
        header = findViewById<View>(R.id.calendar_header) as LinearLayout
        btnPrev = findViewById<View>(R.id.calendar_prev_button) as ImageView
        btnNext = findViewById<View>(R.id.calendar_next_button) as ImageView
        txtDate = findViewById<View>(R.id.calendar_date_display) as TextView
        val sundayTV = findViewById<View>(R.id.sundayTV) as TextView
        val mondayTV = findViewById<View>(R.id.mondayTV) as TextView
        val tuesdayTV = findViewById<View>(R.id.tuesdayTV) as TextView
        val wednesdayTV = findViewById<View>(R.id.wednesdayTV) as TextView
        val thursdayTV = findViewById<View>(R.id.thursdayTV) as TextView
        val fridayTV = findViewById<View>(R.id.fridayTV) as TextView
        val saturdayTV = findViewById<View>(R.id.saturdayTV) as TextView
        txtDate?.also { it.setTextColor(monthColor) }
        sundayTV.also { it.setTextColor(colorDays) }
        mondayTV.also { it.setTextColor(colorDays) }
        tuesdayTV.also { it.setTextColor(colorDays) }
        wednesdayTV.also { it.setTextColor(colorDays) }
        thursdayTV.also { it.setTextColor(colorDays) }
        fridayTV.also { it.setTextColor(colorDays) }
        saturdayTV.also { it.setTextColor(colorDays) }
        grid = findViewById<View>(R.id.calendar_grid) as ExpandableHeightGridView
        (grid as ExpandableHeightGridView).setExpand(true);
    }

    /**
     * Set all onClick event on component
     */
    private fun assignClickHandlers() {
        // add one month and refresh UI
        btnNext?.also {
            it.setOnClickListener {
                currentDate.add(Calendar.MONTH, 1)
                updateCalendar()
            }
        }

        // subtract one month and refresh UI
        btnPrev?.also {
            it.setOnClickListener {
                currentDate.add(Calendar.MONTH, -1)
                updateCalendar()
            }
        }
        grid?.also {
            it.onItemClickListener =
                AdapterView.OnItemClickListener { view, cell, position, _ -> // handle long-press
                    var date = view.getItemAtPosition(position) as Date
                    val cal: Calendar = Calendar.getInstance()
                    cal.time = date
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    date = cal.time
                    var today = Date()
                    cal.time = today
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    today = cal.time
                    if (selectedDates.contains(date)) {
                        selectedDates.remove(date)
                    } else if (!isDisableNextMonthDate || date.after(today) || date.equals(today)) {
                        selectedDates.add(date)
                    }
                    calendarAdapter?.notifyDataSetChanged()
                }
        }
    }

    /**
     * Set events list
     */
    fun setEvents(events: List<Date>) {
        eventsList = events
    }

    fun getSelectedDates(): ArrayList<Date> {
        return selectedDates
    }

    /**
     * Set future dates
     */
    fun isDisableNextfutureDate(status: Boolean) {
        isDisableNextMonthDate = status
    }

    /**
     * Display dates correctly in grid
     */
    /**
     * Display dates correctly in grid
     */
    @SuppressLint("SimpleDateFormat")
    fun updateCalendar() {
        val cells = ArrayList<Date>()
        val calendar = currentDate.clone() as Calendar

        // determine the cell for current month's beginning
        calendar[Calendar.DAY_OF_MONTH] = 1
        val monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 1

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // fill cells
        while (cells.size < DAYS_COUNT) {
            cells.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // update grid
        calendarAdapter = CalendarAdapter(context, cells, eventsList)
        grid?.also {
            it.adapter = calendarAdapter
        }
        // update title
        val sdf = SimpleDateFormat(dateFormat)
        txtDate?.also { it.text = sdf.format(currentDate.time) }
//        calendarAdapter?.notifyDataSetChanged()
    }

    private inner class CalendarAdapter(
        context: Context, days: ArrayList<Date>, // days with events
        private val eventDays: List<Date>
    ) : ArrayAdapter<Date?>(context, R.layout.control_calendar_day, days as List<Date>) {

        // for view inflation
        private var selectedPosition = -1
        private val inflater: LayoutInflater = LayoutInflater.from(context)


        fun setSelectedPosition(position: Int) {
            selectedPosition = position
        }


        override fun getView(position: Int, view: View?, parent: ViewGroup): View {

            var showView = view
            if (showView == null) showView =
                inflater.inflate(R.layout.control_calendar_day, parent, false)
            val itemTextView = showView as FrameLayout
            val textView = showView.findViewById<TextView>(R.id.itemTv)
            val eventHighLight = showView.findViewById<ImageView>(R.id.eventHighLight)
            // getItem(position) date
            val date = getItem(position)
            date?.also {
                val mCalender = DateUtil.getCanlenderFordate(it)
                val day = mCalender.get(Calendar.DAY_OF_MONTH)
                var month = mCalender.get(Calendar.MONTH)
                month++
                val year = mCalender.get(Calendar.YEAR)


                // current view date
                val visibleDate = currentDate.time
                val visibleCalender = DateUtil.getCanlenderFordate(visibleDate)
                var visibleMonth = visibleCalender.get(Calendar.MONTH)
                visibleMonth++
                val visibleYear = visibleCalender.get(Calendar.YEAR)

                // today date
                val today = Date()
                val todayCalender = DateUtil.getCanlenderFordate(today)
                val presentDay = todayCalender.get(Calendar.DAY_OF_MONTH)
                var presentMonth = todayCalender.get(Calendar.MONTH)
                presentMonth++
                val presentYear = todayCalender.get(Calendar.YEAR)


                // inflate item if it does not exist yet

                // if this day has an event, specify event image
                itemTextView.setBackgroundResource(0)
                for (eventDate in eventDays) {
                    val eventDaysCalender = DateUtil.getCanlenderFordate(eventDate)
                    var eventMonth = eventDaysCalender.get(Calendar.MONTH)
                    eventMonth++

                    if (eventDaysCalender.get(Calendar.DAY_OF_MONTH) == day && eventMonth == month && eventDaysCalender.get(
                            Calendar.YEAR
                        ) == year
                    ) {
                        eventHighLight.visibility = View.VISIBLE
                        eventHighLight.setImageResource(R.drawable.circle_dot)
                    }

                }
                textView.setTypeface(null, Typeface.NORMAL)
                textView.setTextColor(disableColorDates)
                if (month == visibleMonth && year == visibleYear) {
                    textView.setTextColor(colorDates)
                    if (day == presentDay && month == presentMonth && year == presentYear) {

                        textView.setTypeface(null, Typeface.BOLD)
                        // itemTextView.setTextColor(ContextCompat.getColor(context, R.color.blue))

                        textView.background = currentDateBackgroundImgDrawable
                    } else if (date.before(today)) {
/*
                        textView.setTextColor(ContextCompat.getColor(context, R.color.border_color))
*/
                        textView.setTextColor(disableColorDates)
                        textView.isEnabled = false

                    } else {
                        /* if (daysCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                         itemTextView.setTextColor(ContextCompat.getColor(context, R.color.worningErrorColor))
                     } else if (daysCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                         itemTextView.setTextColor(ContextCompat.getColor(context, R.color.teal_200))
                     }*/
                    }

                } else {
                    if (isDisableNextMonthDate || date.before(today)) {
                        textView.setTextColor(disableColorDates)
                        textView.isEnabled = false
                    } else {
                        if (date.after(today)) {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.border_color
                                )
                            )
                            textView.isEnabled = false
                        }
                    }

                }


                // set text
                textView.text = mCalender.get(Calendar.DATE).toString()
                if (selectedDates.contains(date)) {
                    showView.setBackgroundResource(R.drawable.custom_circle)
                    textView.setTextColor(selectedColorDates)
                } else {
                    showView.setBackgroundResource(0)
                }

            }
            return showView
        }

    }

    /**
     * Assign event handler to be passed needed events
     */
    fun setEventHandler(eventHandler: EventHandler?) {
        this.eventHandler = eventHandler
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    interface EventHandler {
        fun onDayPress(date: Date?)
    }


    companion object {
        // how many days to show, defaults to six weeks, 42 days
        private const val DAYS_COUNT = 42

        // default date format
        private const val DATE_FORMAT = "MMM yyyy"
    }

    public class CalendarConfig {
        private var calendarView: CalendarView? = null;

        constructor(context: Context, calendarView: CalendarView) {
            this.calendarView = calendarView
            calendarView.dateFormat = CalendarView.DATE_FORMAT
            calendarView.monthColor = ContextCompat.getColor(context, R.color.white)
            calendarView.colorDays = ContextCompat.getColor(context, R.color.white)
            calendarView.colorDates = ContextCompat.getColor(context, R.color.white)
            calendarView.disableColorDates = ContextCompat.getColor(context, R.color.white)
            calendarView.selectedColorDates = ContextCompat.getColor(context, R.color.white)
            calendarView.currentDateBackgroundImgDrawable = ContextCompat.getDrawable(context, R.drawable.reminder)
        }

        constructor(context: Context) : this(context, CalendarView(context)) {
        }

        fun setDateFormat(dateFormat: String): CalendarConfig {
            calendarView?.dateFormat = dateFormat
            return this;
        }

        fun setMonthHeaderColor(monthColor: Int): CalendarConfig {
            calendarView?.monthColor = monthColor
            return this
        }

        fun setDaysColor(daysColor: Int): CalendarConfig {
            calendarView?.colorDays = daysColor
            return this
        }

        fun setDatesColor(datesColor: Int): CalendarConfig {
            calendarView?.colorDates = datesColor
            return this
        }

        fun setDisabledDatesColor(disabledColorDates: Int): CalendarConfig {
            calendarView?.disableColorDates = disabledColorDates
            return this
        }

        fun setSelectedDatesColor(selectedDatesColor: Int): CalendarConfig {
            calendarView?.selectedColorDates = selectedDatesColor
            return this
        }

        fun setCurrentDateBackgroundImage(image: Drawable): CalendarConfig {
            calendarView?.currentDateBackgroundImgDrawable = image
            return this
        }

        fun build(): CalendarView? {
            calendarView?.assignUiElements()
            return calendarView
        }
    }
}


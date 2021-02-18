# CalendarView


## Features

- Date selection - The library provides the calendar logic which enables you to select dates.
- Custom date view - make your day cells look however you want, with any functionality you want.
- Custom calendar view - make your calendar look however you want, with whatever functionality you want.
- Custom current date background - Use any day as the first day of the week.
- Month Year format - Add month and year format for header of calendar view.
- Easily scroll to any date or month view using the date.
- Change event icon position.
- Design your calendar The library provides the logic, you provide the colors.

## Sample project

It's very important to check out the sample app. Most techniques that you would want to implement are already implemented in the examples.


## Setup

The library uses `java.time` classes via [Java 8+ API desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring) for backward compatibility since these classes were added in Java 8.

#### Step 1

To setup your project for desugaring, you need to first ensure that you are using [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin#updating-plugin) 4.0.0 or higher.

Then include the following in your app's build.gradle file:

```groovy
  compileOptions {
    // Flag to enable support for the new language APIs
    coreLibraryDesugaringEnabled true
    // Sets Java compatibility to Java 8
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }

```

#### Step 2

Add calendarlibrary module to your project.

Add the calendarlibrary to your project level `settings.gradle`:

include ':calendarlibrary'

Add CalendarView to your app `build.gradle`:

dependencies {
      implementation project(":calendarlibrary")
}

## Usage

#### Step 1

Add CalendarView to your XML like any other view.
See all available [attributes](#attributes).

```xml
 <com.gwl.calendar.CalendarView
        android:id="@+id/calendarView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="@dimen/_12dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        calendarNS:dateFormat="MMMM yyyy"
        calendarNS:colorMonth="@color/blue"
        calendarNS:colorDays="@color/blue"
        calendarNS:colorDates="@color/dark_gray"
        calendarNS:disableColorDates="@color/light_gray"
        calendarNS:selectedColorDates="@color/white"
        calendarNS:currentDateBackgroundImg="@drawable/reminder"
        calendarNS:eventImageDrawable="@drawable/circle_red_dot"
        calendarNS:eventIconPosition="RIGHT"
        />
```

#### Step 2

Setup the calendar view in your Fragment or Activity:

```kotlin
val calendarView = findViewById<CalendarView>(R.id.calendarView)
CalendarConfig(this, calendarView)
            .setDatesColor(ContextCompat.getColor(this, R.color.dark_gray))
            .setDaysColor(ContextCompat.getColor(this, R.color.blue))
            .setDisabledDatesColor(ContextCompat.getColor(this, R.color.light_gray))
            .setMonthHeaderColor(ContextCompat.getColor(this, R.color.blue))
            .setSelectedDatesColor(ContextCompat.getColor(this, R.color.white))
            .setCurrentDateBackgroundImage(ContextCompat.getDrawable(this, R.drawable.ic_redright_arrow))
            .setEventImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle_red_dot))
            .setEventIconPosition(CalendarConfig.EventIconPosition.TOP_RIGHT)
            .build()
```
Add events: 
```kotlin
var date = mutableListOf<Date>()
        date.add(Calendar.getInstance().time)
        calendarView.setEvents(date)
        calendarView.updateCalendar()
```
Disable Next Future Date:
```kotlin
       calendarView.isDisableNextfutureDate(true)
```

Get selected dates : 
```kotlin
calendarView.getSelectedDates()
```

### Attributes

#### XML (All prefixed `cv_` for clarity)

- **dateFormat**: The xml resource that is inflated and used to date format on header.

- **colorMonth** : The xml resource that is inflated and used to change month color on header.

- **colorDays** : The xml resource that is inflated and used to color of weekdays.

- **colorDates** : The xml resource that is inflated and used to color of dates.

- **disableColorDates** : The xml resource that is inflated and used to color of disable old dates from current date.

- **selectedColorDates** : The xml resource that is inflated and used to color of selected date.

- **currentDateBackgroundImg** : The xml resource that is inflated and used to give background of current date.

- **eventIconPosition** : The xml resource that is inflated and used to give position of event icon.
- 
- **eventImageDrawable** : The xml resource that is inflated and used to drawable of event icon.

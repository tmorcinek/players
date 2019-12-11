package com.morcinek.players.core.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toDayOfWeekDateFormat() = dayOfWeekDateFormat().format(time)
fun Calendar.toStandardString() = standardDateFormat().format(time)

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.dayOfYear: Int
    get() = get(Calendar.DAY_OF_YEAR)
    set(value) = set(Calendar.DAY_OF_YEAR, value)

val Calendar.weekOfYear: Int
    get() = get(Calendar.WEEK_OF_YEAR)

var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) = set(Calendar.DAY_OF_WEEK, value)

var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

fun calendar(time: Long) = Calendar.getInstance().apply { timeInMillis = time }

fun standardDateFormat() = SimpleDateFormat("dd.MM.yyyy", Locale.US)
fun dayOfWeekDateFormat() = SimpleDateFormat("EEEE dd.MM.yyyy", Locale.US)

fun DateFormat.formatCalendar(calendar: Calendar): String = format(calendar.time)
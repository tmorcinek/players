package com.morcinek.players.core.extensions

import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.Fragment
import java.util.*


fun Context.datePickerDialog(calendar: Calendar, updatedDate: (Calendar) -> Unit) = DatePickerDialog(
    this,
    DatePickerDialog.OnDateSetListener { _, updatedYear, updatedMonth, updatedDayOfMonth ->
        updatedDate(Calendar.getInstance().apply {
            year = updatedYear
            month = updatedMonth
            dayOfMonth = updatedDayOfMonth
        })
    },
    calendar.year,
    calendar.month,
    calendar.dayOfMonth
)

fun Fragment.showDatePickerDialog(calendar: Calendar, updatedDate: (Calendar) -> Unit) =
    requireContext().datePickerDialog(calendar, updatedDate).show()

fun Fragment.showYearFirstDatePickerDialog(calendar: Calendar, updatedDate: (Calendar) -> Unit) =
    requireContext().datePickerDialog(calendar, updatedDate).apply { datePicker.touchables[0].performClick() }.show()
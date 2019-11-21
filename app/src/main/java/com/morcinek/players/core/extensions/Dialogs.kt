package com.morcinek.players.core.extensions

import android.app.DatePickerDialog
import android.content.Context
import java.util.*


fun Context.showDatePickerDialog(calendar: Calendar, updatedDate: (Calendar) -> Unit) {
    DatePickerDialog(
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
    ).show()

}
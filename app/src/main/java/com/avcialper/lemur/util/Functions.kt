package com.avcialper.lemur.util

import com.avcialper.owlcalendar.data.models.LineSelectedDate
import com.avcialper.owlcalendar.data.models.StartDate
import java.util.Calendar
import java.util.Locale

private val LOCALE = Locale("tr", "TR")

fun formatDate(dayOfMonth: Int, month: Int, year: Int): String =
    String.format(
        LOCALE,
        "%02d.%02d.%04d",
        dayOfMonth,
        month.plus(1),
        year
    )

fun formatDate(date: LineSelectedDate): String {
    val (year, month, dayOfMonth) = date
    return formatDate(dayOfMonth, month, year)
}

fun formatDate(date: StartDate): String {
    val (year, month, dayOfMonth) = date
    return formatDate(dayOfMonth, month, year)
}

fun formatTime(hour: Int, minute: Int): String =
    String.format(
        LOCALE,
        "%02d:%02d",
        hour,
        minute
    )

fun concatStartAndEndDate(startDate: String, endDate: String): String =
    String.format(
        LOCALE,
        "%s - %s",
        startDate,
        endDate
    )

fun concatStartAndEntTime(startTime: String, endTime: String): String =
    String.format(
        LOCALE,
        "%s - %s",
        startTime,
        endTime
    )

fun formatTime(startTime: String, endTime: String): String =
    String.format(LOCALE, "%s - %s", startTime, endTime)

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    return formatDate(dayOfMonth, month, year)
}

package com.avcialper.lemur.util

import android.content.Context
import android.net.Uri
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.owlcalendar.data.models.LineSelectedDate
import com.avcialper.owlcalendar.data.models.StartDate
import java.io.File
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

fun formatTime(startTime: String, endTime: String): String =
    String.format(LOCALE, "%s - %s", startTime, endTime)
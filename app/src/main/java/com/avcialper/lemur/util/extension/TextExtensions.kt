package com.avcialper.lemur.util.extension

import android.content.Context
import android.text.Editable

fun Editable.toFixedString(): String = this.toString().trim()

fun String.formatInvalidLengthError(
    context: Context,
    labelId: Int,
    minLength: Int,
    maxLength: Int
): String {
    val label = context.getString(labelId)
    return this
        .replace("{0}", label, true)
        .replace("{1}", minLength.toString(), true)
        .replace("{2}", maxLength.toString(), true)
}

fun String.formatInvalidLengthError(
    context: Context,
    labelId: Int,
    maxLength: Int
): String {
    val label = context.getString(labelId)
    return this
        .replace("{0}", label, true)
        .replace("{1}", maxLength.toString(), true)
}

fun String.formatInvalidMinLengthError(
    context: Context,
    labelId: Int,
    minLength: Int
): String {
    val label = context.getString(labelId)
    return this
        .replace("{0}", label, true)
        .replace("{1}", minLength.toString(), true)
}
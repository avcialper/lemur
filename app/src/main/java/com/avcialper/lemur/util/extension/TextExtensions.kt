package com.avcialper.lemur.util.extension

import android.text.Editable

fun Editable.toFixedString(): String = this.toString().trim()
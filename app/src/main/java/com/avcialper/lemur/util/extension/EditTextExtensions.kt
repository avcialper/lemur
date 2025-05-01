package com.avcialper.lemur.util.extension

import android.widget.EditText

fun EditText.toFixedString(): String = this.text.toString().trim()
package com.avcialper.lemur.helper.validator

import android.util.Patterns
import androidx.annotation.StringRes
import com.avcialper.lemur.R

class EmailRule(
    @StringRes override val errorMessage: Int = R.string.invalid_email
) : ValidationRule(predicate = { Patterns.EMAIL_ADDRESS.matcher(it).matches() })
package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class LengthRule(
    @StringRes override val errorMessage: Int = R.string.invalid_length,
) : ValidationRule(predicate = { it.length in 8..24 })
package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class LengthRule(
    min: Int = 8,
    max: Int = 24,
    @StringRes override val errorMessage: Int = R.string.invalid_length,
) : ValidationRule(predicate = { it.length in min..max })
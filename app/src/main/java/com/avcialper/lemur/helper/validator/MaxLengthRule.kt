package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class MaxLengthRule(
    maxLength: Int,
    @StringRes override val errorMessage: Int = R.string.invalid_max_length
) : ValidationRule(predicate = { it.length <= maxLength })
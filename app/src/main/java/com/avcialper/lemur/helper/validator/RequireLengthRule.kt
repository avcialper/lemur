package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class RequireLengthRule(
    requireLength: Int,
    @StringRes override val errorMessage: Int = R.string.invalid_require_length
) : ValidationRule(predicate = { it.length == requireLength })
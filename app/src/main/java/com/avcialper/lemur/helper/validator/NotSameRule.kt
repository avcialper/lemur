package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class NotSameRule(
    private val other: String,
    @StringRes
    override val errorMessage: Int = R.string.is_same_password
) : ValidationRule(predicate = { it != other })
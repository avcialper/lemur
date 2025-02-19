package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import androidx.core.util.Predicate
import com.avcialper.lemur.R

open class ValidationRule(
    @StringRes open val errorMessage: Int = R.string.is_required,
    val predicate: Predicate<String>
)
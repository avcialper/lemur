package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class EmptyRule(
    @StringRes override val errorMessage: Int = R.string.is_required
) : ValidationRule(predicate = { it.isNotEmpty() })
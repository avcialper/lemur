package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class ConfirmPasswordRule(
    private val password: String,
    @StringRes override val errorMessage: Int = R.string.invalid_confirm_password
) : ValidationRule(predicate = { it == password })
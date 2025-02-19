package com.avcialper.lemur.helper.validator

import androidx.annotation.StringRes
import com.avcialper.lemur.R

class PasswordRule(
    @StringRes override val errorMessage: Int = R.string.invalid_password
) : ValidationRule(predicate = {
    val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$")
    it.matches(regex)
})
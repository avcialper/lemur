package com.avcialper.lemur.helper.validator

import androidx.core.widget.doAfterTextChanged
import com.avcialper.lemur.util.extension.toFixedString
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.validate(
    rules: List<ValidationRule>,
    formatErrorMessage: ((String) -> String)? = null
): Boolean {

    val layout = this.parent.parent as TextInputLayout

    this.doAfterTextChanged {
        layout.error = null
    }

    val input = this.toFixedString()
    for (i in rules.indices) {
        val rule = rules[i]
        val isNotValid = rule.predicate.test(input).not()
        val message = rule.errorMessage

        if (isNotValid) {
            val errorMessage = context.resources.getString(message)
            layout.error = formatErrorMessage?.invoke(errorMessage) ?: errorMessage
            return false
        }
    }

    return true
}
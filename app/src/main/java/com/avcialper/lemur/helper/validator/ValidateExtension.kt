package com.avcialper.lemur.helper.validator

import androidx.core.widget.doAfterTextChanged
import com.avcialper.lemur.util.extension.toFixedString
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.validate(
    rules: List<ValidationRule>,
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
            layout.error = context.resources.getString(message)
            return false
        }
    }

    return true
}
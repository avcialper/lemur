package com.avcialper.lemur.ui.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentTextInputBinding
import com.avcialper.lemur.helper.validator.ValidationRule
import com.avcialper.lemur.helper.validator.validate
import com.avcialper.lemur.util.extension.toFixedString
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val layoutInflater = LayoutInflater.from(context)
    private val binding = ComponentTextInputBinding.inflate(layoutInflater, this, true)

    private val input: TextInputEditText
        get() = binding.provider.editText as TextInputEditText
    var value
        get() = input.toFixedString()
        set(value) = input.setText(value)

    fun validate(rules: List<ValidationRule>): Boolean = input.validate(rules)

    fun setLoadingState(isLoading: Boolean) {
        input.apply {
            isEnabled = !isLoading
            alpha = if (isLoading) 0.5f else 1f
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.TextInput, defStyleAttr, 0) {

            // TextInputLayout attrs
            val aErrorIconDrawable = getDrawable(R.styleable.TextInput_errorIconDrawable)
            val aEndIconTint = getColorStateList(R.styleable.TextInput_endIconTint)
            val aEndIconMode = getInt(R.styleable.TextInput_endIconMode, 0)
            val isHaveCounter = getBoolean(R.styleable.TextInput_have_counter, false)
            val counterMaxLength = getInt(R.styleable.TextInput_counter_max_length, 0)

            // TextInputEditText attrs
            val aAutofillHints = getString(R.styleable.TextInput_android_autofillHints)
            val aHint = getString(R.styleable.TextInput_android_hint)
            val aInputType =
                getInt(
                    R.styleable.TextInput_android_inputType,
                    EditorInfo.TYPE_TEXT_VARIATION_NORMAL
                )
            val aImeOptions =
                getInt(R.styleable.TextInput_android_imeOptions, EditorInfo.IME_ACTION_NEXT)

            binding.provider.apply {
                errorIconDrawable = aErrorIconDrawable
                setEndIconTintList(aEndIconTint)
                endIconMode = aEndIconMode
                isCounterEnabled = isHaveCounter
                this.counterMaxLength = counterMaxLength
            }

            input.apply {
                endIconMode = END_ICON_NONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setAutofillHints(aAutofillHints)
                hint = aHint
                inputType = aInputType
                imeOptions = aImeOptions
            }

        }
    }
}
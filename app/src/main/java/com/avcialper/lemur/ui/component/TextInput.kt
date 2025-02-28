package com.avcialper.lemur.ui.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentTextInputBinding
import com.avcialper.lemur.helper.validator.ValidationRule
import com.avcialper.lemur.helper.validator.validate
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
        get() = input.text.toString().trim()
        set(value) = input.setText(value)

    fun validate(rules: List<ValidationRule>): Boolean = input.validate(rules)

    fun setLoadingState(isLoading: Boolean) {
        input.apply {
            isEnabled = !isLoading
            alpha = if (isLoading) 0.5f else 1f
        }
    }

    fun onTextChanged(listener: (String) -> Unit) {
        input.addTextChangedListener { text ->
            listener.invoke(text.toString())
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TextInput, defStyleAttr, 0)

        // TextInputLayout attrs
        val aErrorIconDrawable = a.getDrawable(R.styleable.TextInput_errorIconDrawable)
        val aEndIconTint = a.getColorStateList(R.styleable.TextInput_endIconTint)
        val aEndIconMode = a.getInt(R.styleable.TextInput_endIconMode, 0)

        // TextInputEditText attrs
        val aAutofillHints = a.getString(R.styleable.TextInput_android_autofillHints)
        val aHint = a.getString(R.styleable.TextInput_android_hint)
        val aInputType =
            a.getInt(R.styleable.TextInput_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_NORMAL)

        binding.provider.apply {
            errorIconDrawable = aErrorIconDrawable
            setEndIconTintList(aEndIconTint)
            endIconMode = aEndIconMode
        }

        input.apply {
            endIconMode = END_ICON_NONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setAutofillHints(aAutofillHints)
            hint = aHint
            inputType = aInputType
        }

        a.recycle()
    }
}
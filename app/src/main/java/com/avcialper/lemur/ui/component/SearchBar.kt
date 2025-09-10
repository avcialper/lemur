package com.avcialper.lemur.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentSearchBarBinding
import com.avcialper.lemur.helper.SimplifiedTextWatcher
import com.avcialper.lemur.util.extension.toFixedString

@SuppressLint("PrivateResource")
class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        ComponentSearchBarBinding.inflate(LayoutInflater.from(context), this)

    init {
        context.withStyledAttributes(attrs, R.styleable.SearchBar, defStyleAttr, 0) {
            val hint = getString(R.styleable.SearchBar_android_hint)
            binding.componentEditText.hint = hint
        }
    }

    fun addSearchTextChangedListener(listener: (String) -> Unit) {
        binding.componentEditText.addTextChangedListener(object : SimplifiedTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toFixedString() ?: ""
                listener(text)
            }
        })
    }

}
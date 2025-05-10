package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentHeaderAndDividerBinding

class HeaderAndDivider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding =
        ComponentHeaderAndDividerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.HeaderAndDivider, defStyleAttr, 0) {
            val label = getString(R.styleable.HeaderAndDivider_label)
            if (label == null)
                binding.label.visibility = View.GONE
            binding.label.text = label
        }
    }

}
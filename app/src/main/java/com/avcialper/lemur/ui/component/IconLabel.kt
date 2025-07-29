package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentIconLabelBinding


class IconLabel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val ANIMATION_DURATION: Long = 500
    }

    private val layoutInflater = LayoutInflater.from(context)
    private val binding = ComponentIconLabelBinding.inflate(layoutInflater, this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.IconLabel, defStyleAttr, 0) {

            val label = getString(R.styleable.IconLabel_android_label)
            val icon = getDrawable(R.styleable.IconLabel_left_icon)

            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(R.attr.primaryColor, typedValue, true)
            val defTextColor = typedValue.data

            val textColor = getColor(R.styleable.IconLabel_text_color, defTextColor)

            binding.apply {
                textLabel.text = label
                imageIcon.setImageDrawable(icon)
                textLabel.setTextColor(textColor)
            }

        }
    }

    fun animatedIconUpdate(iconId: Int) = with(binding.imageIcon) {
        fadeAnimation {
            setImageResource(iconId)
        }
    }

    fun updateIcon(iconId: Int) {
        binding.imageIcon.setImageResource(iconId)
    }

    fun updateIconAndLabel(labelId: Int, iconId: Int) {
        binding.apply {
            textLabel.text = context.getString(labelId)
            imageIcon.setImageResource(iconId)
        }
    }

    private fun View.fadeAnimation(job: () -> Unit) {
        animate().alpha(0f).setDuration(ANIMATION_DURATION).withEndAction {
            job()
            animate().alpha(1f).setDuration(ANIMATION_DURATION).start()
        }.start()
    }

    fun isSameLabel(labelId: Int): Boolean {
        val label = context.getString(labelId)
        return binding.textLabel.text.toString() == label
    }

    fun handleLoading(isLoading: Boolean) = with(binding) {
        root.alpha = if (isLoading) 0.5f else 1f
        root.isClickable = isLoading
    }

}
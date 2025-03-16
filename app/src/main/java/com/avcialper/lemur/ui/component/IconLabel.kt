package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.IconLabel, defStyleAttr, 0)

        val label = a.getString(R.styleable.IconLabel_android_label)
        val icon = a.getDrawable(R.styleable.IconLabel_left_icon)

        binding.apply {
            textLabel.text = label
            imageIcon.setImageDrawable(icon)
        }

        a.recycle()
    }

    fun animatedUpdate(labelId: Int, iconId: Int, onClick: (() -> Unit)?) = with(binding) {
        root.fadeAnimation {
            textLabel.text = context.getString(labelId)
            imageIcon.setImageResource(iconId)
        }
        if (onClick != null)
            setOnClickListener { onClick.invoke() }
        else
            setOnClickListener(null)
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

}
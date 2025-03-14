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

    // Could not compare icon drawable, just check label
    fun isDifferent(label: Int): Boolean {
        val newLabel = context.getString(label)
        return newLabel != binding.textLabel.text
    }

    fun updateAll(label: Int, icon: Int, onClick: (() -> Unit)?) = with(binding) {
        fadeAnimation(root) {
            textLabel.text = context.getString(label)
            imageIcon.setImageResource(icon)
        }
        setOnClickListener { onClick?.invoke() }
    }

    fun updateIcon(icon: Int) = with(binding.imageIcon) {
        fadeAnimation(this) {
            setImageResource(icon)
        }
    }

    private fun fadeAnimation(view: View, job: () -> Unit) {
        view.animate().alpha(0f).setDuration(ANIMATION_DURATION).withEndAction {
            job()
            view.animate().alpha(1f).setDuration(ANIMATION_DURATION).start()
        }.start()
    }

}
package com.avcialper.lemur.ui.tasks

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ItemFilterTypeBinding
import com.avcialper.lemur.util.constant.FilterType

class FilterViewHolder(
    private val binding: ItemFilterTypeBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context
    private val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.filter_type_background)
            ?.mutate() as GradientDrawable

    fun bind(position: Int, title: String?, isSelected: Boolean, onClickListener: (Int) -> Unit) =
        with(binding) {
            val type = FilterType.fromIndex(position)
            val text = context.getString(type.value)
            val isLastItem = position == FilterType.size - 1

            textType.apply {
                // If the title is null, use the text instead (date filter)
                this.text = title ?: text
                setOnClickListener { onClickListener(position) }
                setMargins(isLastItem)
                setBackgroundColor(isSelected)
                setTypeTextColor(isSelected)
            }
        }

    private fun TextView.setMargins(isLastItem: Boolean) {
        val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
        val margin = layoutParams.leftMargin
        layoutParams.rightMargin = if (isLastItem) margin else 0
        this.layoutParams = layoutParams
    }

    private fun TextView.setBackgroundColor(isSelected: Boolean) {
        val backgroundColorId = if (isSelected) R.color.orange else R.color.transparent
        val backgroundColor = ContextCompat.getColor(context, backgroundColorId)
        backgroundDrawable.setColor(backgroundColor)
        background = backgroundDrawable
    }

    private fun TextView.setTypeTextColor(isSelected: Boolean) {
        val color = if (isSelected)
            ContextCompat.getColor(this@FilterViewHolder.context, R.color.black)
        else {
            val typedValue = TypedValue()
            val theme = this@FilterViewHolder.context.theme
            theme.resolveAttribute(R.attr.primaryColor, typedValue, true)
            typedValue.data
        }
        setTextColor(color)
    }

}
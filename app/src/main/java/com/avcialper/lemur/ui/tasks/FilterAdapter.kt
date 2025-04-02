package com.avcialper.lemur.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.databinding.ItemFilterTypeBinding
import com.avcialper.lemur.util.constant.FilterType

class FilterAdapter(
    startType: FilterType?,
    var title: String?,
    private val onFilterChangeListener: (FilterType, String?) -> Unit
) : RecyclerView.Adapter<FilterViewHolder>() {

    private var selectedPosition = startType?.ordinal ?: FilterType.ALL.ordinal
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterTypeBinding.inflate(layoutInflater, parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(position, title, isSelected, ::changeSelectedPosition)
    }

    override fun getItemCount(): Int = FilterType.size

    private fun changeSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(oldPosition)
        notifyItemChanged(position)

        val type = FilterType.fromIndex(position)
        val titleId = type.value
        if (type != FilterType.DATE || title?.contains(".") == false)
            title = context?.getString(titleId)
        onFilterChangeListener.invoke(type, title)
    }
}
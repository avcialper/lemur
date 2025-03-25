package com.avcialper.lemur.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.databinding.ItemFilterTypeBinding
import com.avcialper.lemur.util.constant.FilterType

class FilterAdapter(
    startType: FilterType,
    private val onFilterChangeListener: (FilterType) -> Unit
) : RecyclerView.Adapter<FilterViewHolder>() {

    private var selectedPosition = startType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterTypeBinding.inflate(layoutInflater, parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(position, isSelected, ::changeSelectedPosition)
    }

    override fun getItemCount(): Int = FilterType.size

    private fun changeSelectedPosition(position: Int) {
        if (position == selectedPosition) return    // Already selected

        val oldPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(oldPosition)
        notifyItemChanged(position)

        val type = FilterType.fromIndex(position)
        onFilterChangeListener.invoke(type)
    }
}
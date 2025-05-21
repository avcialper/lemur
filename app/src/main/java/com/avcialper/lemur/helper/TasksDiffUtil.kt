package com.avcialper.lemur.helper

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.TaskCard

class TasksDiffUtil(
    private val oldList: List<TaskCard>,
    private val newList: List<TaskCard>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
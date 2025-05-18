package com.avcialper.lemur.ui.tasks.detail.note

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.Note

class NoteDiffUtil(
    private val oldData: List<Note>,
    private val newData: List<Note>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}
package com.avcialper.lemur.ui.team.adapter

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.Team

class TeamDiffUtil(
    private val oldData: List<Team>,
    private val newData: List<Team>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].id == newData[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}
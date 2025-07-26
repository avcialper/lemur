package com.avcialper.lemur.ui.team.members.adapter

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.MemberCard

class MemberDiffUtil(private val oldData: List<MemberCard>, private val newData: List<MemberCard>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].id == newData[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}
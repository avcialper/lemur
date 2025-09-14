package com.avcialper.lemur.ui.team.roles.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.MemberCard

class RoleDetailDiffUtil(
    private val oldData: List<MemberCard>,
    private val newData: List<MemberCard>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition] == newData[newItemPosition]

}
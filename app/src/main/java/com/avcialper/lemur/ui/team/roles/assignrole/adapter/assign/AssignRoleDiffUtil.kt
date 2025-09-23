package com.avcialper.lemur.ui.team.roles.assignrole.adapter.assign

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.SelectableMemberCard

class AssignRoleDiffUtil(
    private val oldData: List<SelectableMemberCard>,
    private val newData: List<SelectableMemberCard>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition] == newData[newItemPosition]

}
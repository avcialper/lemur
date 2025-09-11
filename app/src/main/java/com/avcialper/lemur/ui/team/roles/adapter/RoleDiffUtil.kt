package com.avcialper.lemur.ui.team.roles.adapter

import androidx.recyclerview.widget.DiffUtil
import com.avcialper.lemur.data.model.local.Role

class RoleDiffUtil(private val oldData: List<Role>, private val newData: List<Role>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition].code == newData[newItemPosition].code

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition] == newData[newItemPosition]

}
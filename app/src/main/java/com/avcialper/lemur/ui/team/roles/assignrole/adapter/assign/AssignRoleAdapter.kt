package com.avcialper.lemur.ui.team.roles.assignrole.adapter.assign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.MemberSelectorCardBinding

class AssignRoleAdapter(
    members: List<SelectableMemberCard>,
    private val changeSelection: (Boolean, String) -> Unit
) :
    RecyclerView.Adapter<AssignRoleViewHolder>() {

    private var data = members

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignRoleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MemberSelectorCardBinding.inflate(layoutInflater, parent, false)
        return AssignRoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignRoleViewHolder, position: Int) {
        val currentData = data[position]
        holder.bind(currentData) { isSelected ->
            changeSelection(isSelected, currentData.id)
        }
    }

    override fun getItemCount(): Int = data.size

    fun changeData(members: List<SelectableMemberCard>) {
        val diffUtil = AssignRoleDiffUtil(data, members)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        data = members
        diffResult.dispatchUpdatesTo(this)
    }
}
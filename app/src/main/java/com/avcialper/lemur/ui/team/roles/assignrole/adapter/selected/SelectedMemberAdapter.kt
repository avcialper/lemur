package com.avcialper.lemur.ui.team.roles.assignrole.adapter.selected

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.SelectedMemberCardBinding

class SelectedMemberAdapter(
    roles: List<SelectableMemberCard>,
    private val remove: (String) -> Unit
) :
    RecyclerView.Adapter<SelectedMemberViewHolder>() {

    private var data = roles

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SelectedMemberCardBinding.inflate(layoutInflater, parent, false)
        return SelectedMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedMemberViewHolder, position: Int) {
        val currentData = data[position]
        holder.bind(currentData) {
            remove(currentData.id)
        }
    }

    override fun getItemCount(): Int = data.size

    fun changeData(selectableMembers: List<SelectableMemberCard>) {
        val diffUtil = SelectedMemberDiffUtil(data, selectableMembers)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        data = selectableMembers
        diffResult.dispatchUpdatesTo(this)
    }
}
package com.avcialper.lemur.ui.team.component.leadselector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.LeadSelectorCardBinding

class LeadSelectorAdapter(
    members: List<SelectableMemberCard>,
    private val handleLeadSelect: (SelectableMemberCard) -> Unit
) :
    RecyclerView.Adapter<LeadSelectorViewHolder>() {

    private val data = members.filter { member ->
        member.id != UserManager.user?.id
    }
    private var selectedId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadSelectorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LeadSelectorCardBinding.inflate(layoutInflater, parent, false)
        return LeadSelectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeadSelectorViewHolder, position: Int) {
        val member = data[position]
        holder.holder(member) {
            val oldId = selectedId
            selectedId = member.id

            notifyItemChanged(data.indexOfFirst { it.id == oldId })
            notifyItemChanged(position)

            data.map { it.isSelected = it.id == selectedId }

            handleLeadSelect(member)
        }
    }

    override fun getItemCount(): Int = data.size
}
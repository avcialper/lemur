package com.avcialper.lemur.ui.team.component.roleselector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.RoleCard
import com.avcialper.lemur.databinding.ItemRoleSelectorBinding

class RoleSelectorAdapter(
    private val roles: List<RoleCard>,
    private val onItemToggled: (Int, Boolean) -> Unit
) :
    RecyclerView.Adapter<RoleSelectorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleSelectorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRoleSelectorBinding.inflate(layoutInflater, parent, false)
        return RoleSelectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleSelectorViewHolder, position: Int) {
        val role = roles[position]
        holder.bind(role, position, onItemToggled)
    }

    override fun getItemCount(): Int = roles.size
}
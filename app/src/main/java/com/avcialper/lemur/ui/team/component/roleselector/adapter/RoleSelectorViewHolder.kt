package com.avcialper.lemur.ui.team.component.roleselector.adapter

import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.RoleCard
import com.avcialper.lemur.databinding.ItemRoleSelectorBinding

class RoleSelectorViewHolder(private val binding: ItemRoleSelectorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(role: RoleCard, position: Int, onItemToggled: (Int, Boolean) -> Unit) {
        binding.cbSelect.apply {
            text = role.name
            isChecked = role.isSelected

            setOnCheckedChangeListener(null)
            setOnCheckedChangeListener { _, isChecked ->
                onItemToggled(position, isChecked)
            }
        }
    }

}
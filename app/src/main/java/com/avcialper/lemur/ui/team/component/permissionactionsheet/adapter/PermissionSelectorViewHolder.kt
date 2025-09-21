package com.avcialper.lemur.ui.team.component.permissionactionsheet.adapter

import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.SelectablePermission
import com.avcialper.lemur.databinding.ItemPermissionSelectorBinding

class PermissionSelectorViewHolder(private val binding: ItemPermissionSelectorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SelectablePermission, onItemToggled: (Boolean) -> Unit) =
        with(binding) {
            cbPermissionSelect.apply {
                val label = itemView.context.getString(data.permission.label)
                text = label
                isChecked = data.isSelected

                setOnCheckedChangeListener(null)
                setOnCheckedChangeListener { _, isChecked ->
                    onItemToggled(isChecked)
                }
            }
        }
}
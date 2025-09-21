package com.avcialper.lemur.ui.team.component.permissionactionsheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.SelectablePermission
import com.avcialper.lemur.databinding.ItemPermissionSelectorBinding

class PermissionSelectorAdapter(
    private val permissions: List<SelectablePermission>,
    private val onItemToggled: (Int, Boolean) -> Unit
) :
    RecyclerView.Adapter<PermissionSelectorViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PermissionSelectorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPermissionSelectorBinding.inflate(layoutInflater, parent, false)
        return PermissionSelectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionSelectorViewHolder, position: Int) {
        val currentData = permissions[position]
        holder.bind(currentData) { isChecked ->
            onItemToggled(position, isChecked)
        }
    }

    override fun getItemCount(): Int = permissions.size
}
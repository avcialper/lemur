package com.avcialper.lemur.ui.team.roles.adapter

import android.content.res.ColorStateList
import android.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.databinding.RoleCardBinding
import com.avcialper.lemur.util.constant.Permissions
import com.google.android.material.chip.Chip

class RolesViewHolder(private val binding: RoleCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val myChipBackgroundColor =
        ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.chardonnay))

    fun bind(role: Role, onItemClicked: (Role) -> Unit) = with(binding) {
        tvRoleName.text = role.name
        root.setOnClickListener {
            onItemClicked(role)
        }

        chipsPermissions.removeAllViews()

        role.permissions.forEach { permissions ->
            val permissionLabelRes = Permissions.valueOf(permissions).label
            val permissionLabel = itemView.context.getString(permissionLabelRes)

            val chip =
                Chip(ContextThemeWrapper(root.context, R.style.SelectedRoleChip), null, 0).apply {
                    text = permissionLabel
                    chipBackgroundColor = myChipBackgroundColor
                }

            chipsPermissions.addView(chip)
        }
    }
}
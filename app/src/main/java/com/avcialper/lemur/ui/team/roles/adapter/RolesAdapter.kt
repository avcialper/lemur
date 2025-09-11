package com.avcialper.lemur.ui.team.roles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.databinding.RoleCardBinding

class RolesAdapter(roles: List<Role>) : RecyclerView.Adapter<RolesViewHolder>() {

    private var data = roles

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RoleCardBinding.inflate(layoutInflater, parent, false)
        return RolesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {
        val role = data[position]
        holder.bind(role)
    }

    override fun getItemCount(): Int = data.size

    fun changeData(roles: List<Role>) {
        val diffUtil = RoleDiffUtil(data, roles)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        data = roles
        diffResult.dispatchUpdatesTo(this)
    }
}
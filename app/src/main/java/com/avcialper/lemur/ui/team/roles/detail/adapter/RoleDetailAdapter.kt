package com.avcialper.lemur.ui.team.roles.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.RoleMemberCardBinding

class RoleDetailAdapter(roles: List<MemberCard>) : RecyclerView.Adapter<RoleDetailViewHolder>() {

    private var data = roles

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RoleMemberCardBinding.inflate(layoutInflater, parent, false)
        return RoleDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleDetailViewHolder, position: Int) {
        val currentData = data[position]
        holder.bind(currentData)
    }

    override fun getItemCount(): Int = data.size

    fun changeData(roles: List<MemberCard>) {
        val diffUtil = RoleDetailDiffUtil(data, roles)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        data = roles
        diffResult.dispatchUpdatesTo(this)
    }
}
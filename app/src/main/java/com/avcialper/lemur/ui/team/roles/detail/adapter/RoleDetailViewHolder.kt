package com.avcialper.lemur.ui.team.roles.detail.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.RoleMemberCardBinding

class RoleDetailViewHolder(private val binding: RoleMemberCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: MemberCard) = with(binding) {
        textMemberName.text = data.name

        if (data.imageUrl.isNullOrEmpty())
            imageMember.load(R.drawable.logo)
        else
            imageMember.load(data.imageUrl)

    }
}
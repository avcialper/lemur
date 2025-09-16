package com.avcialper.lemur.ui.team.roles.detail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.RoleMemberCardBinding
import com.avcialper.lemur.util.constant.Permissions

class RoleDetailViewHolder(private val binding: RoleMemberCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        data: MemberCard,
        loggedUser: MemberCard?,
        teamLeadId: String,
        removeRoleFromMember: (MemberCard) -> Unit
    ) = with(binding) {
        textMemberName.text = data.name

        if (data.imageUrl.isNullOrEmpty())
            imageMember.load(R.drawable.logo)
        else
            imageMember.load(data.imageUrl)

        val isLoggedUser = loggedUser?.id == data.id
        val isHaveMemberManagementPermission =
            loggedUser?.permissions?.contains(Permissions.MEMBER_MANAGEMENT.name) ?: false

        iconRemoveRole.visibility =
            if (isHaveMemberManagementPermission && !isLoggedUser && data.id != teamLeadId)
                View.VISIBLE
            else
                View.GONE

        iconRemoveRole.setOnClickListener {
            removeRoleFromMember(data)
        }

    }
}
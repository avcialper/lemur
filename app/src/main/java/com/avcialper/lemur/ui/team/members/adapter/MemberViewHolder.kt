package com.avcialper.lemur.ui.team.members.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.ItemMemberCardBinding

class MemberViewHolder(private val binding: ItemMemberCardBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(memberCard: MemberCard, isAdmin: Boolean, removeMember: (Member) -> Unit) =
        with(binding) {
            textUsername.text = memberCard.name
            textRoleName.text = memberCard.role

            if (memberCard.imageUrl != null && memberCard.imageUrl != "")
                imageMember.load(memberCard.imageUrl)

            iconRemoveMember.visibility =
                if (isAdmin && UserManager.user!!.id != memberCard.id)
                    View.VISIBLE
                else
                    View.GONE


            iconRemoveMember.setOnClickListener {
                removeMember(memberCard.toMember())
            }
        }

}
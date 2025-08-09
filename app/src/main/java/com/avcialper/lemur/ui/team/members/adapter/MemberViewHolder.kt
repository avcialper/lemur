package com.avcialper.lemur.ui.team.members.adapter

import android.content.res.ColorStateList
import android.view.ContextThemeWrapper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.ItemMemberCardBinding
import com.google.android.material.chip.Chip

class MemberViewHolder(private val binding: ItemMemberCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val myChipBackgroundColor =
        ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.chardonnay))

    fun bind(memberCard: MemberCard, isAdmin: Boolean, removeMember: (Member) -> Unit) =
        with(binding) {
            textUsername.text = memberCard.name
            memberCard.roleNames.forEach { roleName ->
                val chip = Chip(
                    ContextThemeWrapper(root.context, R.style.SelectedRoleChip),
                    null,
                    0
                ).apply {
                    text = roleName
                    chipBackgroundColor = myChipBackgroundColor
                }

                chipsRoles.addView(chip)
            }

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
package com.avcialper.lemur.ui.team.roles.assignrole.adapter.assign

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.MemberSelectorCardBinding

class AssignRoleViewHolder(private val binding: MemberSelectorCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SelectableMemberCard, changeSelection: (Boolean) -> Unit) = with(binding) {
        textMemberName.text = data.name
        imageIsSelected.visibility = if (data.isSelected) View.VISIBLE else View.GONE

        if (data.imageUrl.isNullOrEmpty())
            imageMember.load(R.drawable.logo)
        else
            imageMember.load(data.imageUrl)

        root.setOnClickListener {
            changeSelection(!data.isSelected)
        }

    }
}
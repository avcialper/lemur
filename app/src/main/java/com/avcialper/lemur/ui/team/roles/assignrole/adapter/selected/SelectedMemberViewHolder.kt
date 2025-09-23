package com.avcialper.lemur.ui.team.roles.assignrole.adapter.selected

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.SelectedMemberCardBinding

class SelectedMemberViewHolder(private val binding: SelectedMemberCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SelectableMemberCard, remove: () -> Unit) = with(binding) {
        textMemberName.text = data.name

        if (data.imageUrl.isNullOrEmpty())
            imageMember.load(R.drawable.logo)
        else
            imageMember.load(data.imageUrl)

        root.setOnClickListener {
            remove()
        }
    }
}
package com.avcialper.lemur.ui.team.component.leadselector.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.LeadSelectorCardBinding

class LeadSelectorViewHolder(private val binding: LeadSelectorCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun holder(member: SelectableMemberCard, handleSelect: () -> Unit) = with(binding) {

        val (imageUrl, id, name, isSelected) = member

        if (!imageUrl.isNullOrEmpty())
            imageMember.load(imageUrl)

        textUsername.text = name
        imageIsSelected.visibility = if (isSelected) View.VISIBLE else View.GONE

        root.setOnClickListener {
            handleSelect()
        }
    }

}
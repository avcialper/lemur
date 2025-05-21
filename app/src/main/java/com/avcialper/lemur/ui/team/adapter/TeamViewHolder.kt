package com.avcialper.lemur.ui.team.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.databinding.TeamCardBinding

class TeamViewHolder(private val binding: TeamCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(team: TeamCard, navigate: (String) -> Unit) = with(binding) {
        val (id, name, description, image) = team

        root.setOnClickListener {
            navigate(id)
        }

        image?.let {
            teamImage.load(it)
        }
        teamName.text = name
        teamDescription.text = description
    }

}
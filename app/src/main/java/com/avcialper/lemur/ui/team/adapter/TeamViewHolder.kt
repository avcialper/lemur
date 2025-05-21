package com.avcialper.lemur.ui.team.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.databinding.TeamCardBinding

class TeamViewHolder(private val binding: TeamCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(team: Team, navigate: (Team) -> Unit) = with(binding) {
        val (_, _, name, description, image) = team

        root.setOnClickListener {
            navigate(team)
        }

        image?.let {
            teamImage.load(it)
        }
        teamName.text = name
        teamDescription.text = description
    }

}
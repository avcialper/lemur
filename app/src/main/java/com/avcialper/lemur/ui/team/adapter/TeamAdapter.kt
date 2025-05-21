package com.avcialper.lemur.ui.team.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.databinding.TeamCardBinding

class TeamAdapter(teams: List<Team>, private val navigate: (Team) -> Unit) :
    RecyclerView.Adapter<TeamViewHolder>() {

    private var data = teams

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TeamCardBinding.inflate(layoutInflater, parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = data[position]
        holder.bind(team, navigate)
    }

    override fun getItemCount(): Int = data.size

    fun changeList(data: List<Team>) {
        val diffUtil = TeamDiffUtil(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.data = data
        diffResult.dispatchUpdatesTo(this)
    }
}
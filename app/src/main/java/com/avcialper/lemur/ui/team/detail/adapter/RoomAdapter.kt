package com.avcialper.lemur.ui.team.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.databinding.ItemRoomBinding

class RoomAdapter(private val rooms: List<Room>, private val onRoomClick: (String) -> Unit): RecyclerView.Adapter<RoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRoomBinding.inflate(layoutInflater, parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.bind(room, onRoomClick)
    }

    override fun getItemCount(): Int = rooms.size
}
package com.avcialper.lemur.ui.team.detail.adapter

import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.databinding.ItemRoomBinding

class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(room: Room, onRoomClick: (String) -> Unit) = with(binding){
        roomName.text = room.name
        roomDescription.text = room.description
        root.setOnClickListener {
            onRoomClick(room.id)
        }
    }

}
package com.avcialper.lemur.ui.tasks.detail.note

import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.databinding.NoteCardBinding
import com.avcialper.lemur.util.concatDateAndTime

class NoteViewHolder(private val binding: NoteCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(note: Note) = with(binding) {
        txNote.text = note.note
        dateAndTime.text = concatDateAndTime(note.date, note.time)
    }
}
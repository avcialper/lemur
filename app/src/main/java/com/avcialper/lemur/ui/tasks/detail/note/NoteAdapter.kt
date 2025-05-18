package com.avcialper.lemur.ui.tasks.detail.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.databinding.NoteCardBinding

class NoteAdapter(notes: List<Note>) : RecyclerView.Adapter<NoteViewHolder>() {

    private var data = notes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NoteCardBinding.inflate(layoutInflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = data[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = data.size

    fun setData(notes: List<Note>) {
        val diffCallback = NoteDiffUtil(data, notes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = notes
        diffResult.dispatchUpdatesTo(this)
    }
}
package com.avcialper.lemur.ui.component.tasksarea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTaskBinding

class TaskRecyclerView(private val tasks: List<Task>) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = ComponentTaskBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = if (tasks.size > 3) 3 else tasks.size
}
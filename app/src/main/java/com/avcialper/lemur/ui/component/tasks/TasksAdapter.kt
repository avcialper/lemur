package com.avcialper.lemur.ui.component.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.databinding.ComponentTaskBinding
import com.avcialper.lemur.helper.TasksDiffUtil

class TasksAdapter(
    private var tasks: List<TaskCard>,
    private val isShort: Boolean = false,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<TasksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ComponentTaskBinding.inflate(layoutInflater, parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onClick)
    }

    override fun getItemCount(): Int = if (isShort && tasks.size > 3) 3 else tasks.size

    fun changeList(tasks: List<TaskCard>) {
        val diffCallback = TasksDiffUtil(this.tasks, tasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.tasks = tasks
        diffResult.dispatchUpdatesTo(this)
    }
}
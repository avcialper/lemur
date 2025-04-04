package com.avcialper.lemur.ui.component.tasks

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTaskBinding
import com.avcialper.lemur.util.constant.TaskStatus
import java.util.Locale

class TasksViewHolder(
    private val binding: ComponentTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) = with(binding) {
        textTaskName.text = task.name
        textDescription.text = task.description

        handleDateFormat(task)
        handleTaskStatus(task.status)
    }

    private fun handleDateFormat(task: Task) = with(binding) {
        val (_, _, _, startDate, endDate, startTime, endTime, _, _) = task

        if (endDate != null)
            textDateTime.text = String.format(Locale.getDefault(), "%s - %s", startDate, endDate)
        else if (endTime != null)
            textDateTime.text =
                String.format(Locale.getDefault(), "%s / %s - %s", startDate, startTime, endTime)
        else
            textDateTime.text = String.format(Locale.getDefault(), "%s - %s", startDate, startTime)
    }

    private fun handleTaskStatus(status: TaskStatus) = with(binding) {
        val colorId = when (status) {
            TaskStatus.CONTINUES -> R.color.orange
            TaskStatus.COMPLETED -> R.color.chateau_green
            TaskStatus.CANCELED -> R.color.red
        }
        val statusDrawable =
            ContextCompat.getDrawable(root.context, R.drawable.oval_background)?.mutate()
        statusDrawable?.let {
            val color = ContextCompat.getColor(root.context, colorId)
            DrawableCompat.setTint(it, color)
        }
        textTaskName.setCompoundDrawablesWithIntrinsicBounds(null, null, statusDrawable, null)
    }

}
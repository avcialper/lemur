package com.avcialper.lemur.ui.component.tasks

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTaskBinding
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import java.util.Locale

class TasksViewHolder(
    private val binding: ComponentTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) = with(binding) {
        textTaskName.text = task.name
        textDescription.text = task.description

        handleDateFormat(task)
        handleTaskStatus(task.status)
        handleTaskType(task.type)
    }

    private fun handleDateFormat(task: Task) = with(binding) {
        val (_, _, _, _, startDate, endDate, startTime, endTime, _, _, _) = task

        if (endDate == null)
            textDateTime.text =
                String.format(Locale.getDefault(), "%s / %s - %s", startDate, startTime, endTime)
        else
            textDateTime.text = String.format(
                Locale.getDefault(),
                "%s - %s / %s - %s",
                startDate,
                endDate,
                startTime,
                endTime
            )
    }

    private fun handleTaskStatus(status: TaskStatus) = with(binding) {
        val colorId = when (status) {
            TaskStatus.CONTINUES -> R.color.orange
            TaskStatus.COMPLETED -> R.color.chateau_green
            TaskStatus.CANCELED -> R.color.red
        }
        val statusDrawable = getDrawable(R.drawable.oval_background)
        statusDrawable?.let {
            val color = ContextCompat.getColor(root.context, colorId)
            DrawableCompat.setTint(it, color)
        }
        textTaskName.setRightDrawable(statusDrawable)
    }

    private fun handleTaskType(type: TaskType) = with(binding) {
        val iconId = when (type) {
            TaskType.PERSONAL -> R.drawable.ic_profile
            TaskType.TEAM -> R.drawable.ic_team
            TaskType.MEET -> R.drawable.ic_video_call
        }

        val drawable = getDrawable(iconId)
        textDateTime.setRightDrawable(drawable)
    }

    private fun getDrawable(id: Int): Drawable? =
        ContextCompat.getDrawable(binding.root.context, id)?.mutate()

    private fun TextView.setRightDrawable(drawable: Drawable?) {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}
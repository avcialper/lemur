package com.avcialper.lemur.ui.component.tasksarea

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTaskBinding
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import java.util.Locale

class TaskViewHolder(private val binding: ComponentTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Task) = with(binding) {
        val (name, description, startDate, endDate, startTime, endTime, type, status) = item
        textTaskName.text = name
        textDescription.text = description

        if (endDate != null)
            textDateTime.text = String.format(Locale.getDefault(), "%s - %s", startDate, endDate)
        else if (endTime != null)
            textDateTime.text =
                String.format(Locale.getDefault(), "%s / %s - %s", startDate, startTime, endTime)
        else
            textDateTime.text = String.format(Locale.getDefault(), "%s - %s", startDate, startTime)

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
        textDateTime.setCompoundDrawablesWithIntrinsicBounds(null, null, statusDrawable, null)

        val drawableId = when (type) {
            TaskType.PERSONAL -> R.drawable.ic_profile
            TaskType.MEET -> R.drawable.ic_video_call
            TaskType.TEAM -> R.drawable.ic_team
        }
        textTaskName.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0)
    }

}
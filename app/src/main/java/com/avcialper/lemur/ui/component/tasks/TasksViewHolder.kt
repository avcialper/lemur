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

    fun bind(task: Task, onClick: (Task) -> Unit) = with(binding) {
        textTaskName.text = task.subject
        textDescription.text = task.description

        root.setOnClickListener { onClick.invoke(task) }

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
        val statusDrawable = getDrawable(R.drawable.oval_background)
        statusDrawable?.let {
            val color = ContextCompat.getColor(root.context, status.colorId)
            DrawableCompat.setTint(it, color)
        }
        textTaskName.setRightDrawable(statusDrawable)
    }

    private fun handleTaskType(type: TaskType) = with(binding) {
        val drawable = getDrawable(type.drawableId)
        textDateTime.setRightDrawable(drawable)
    }

    private fun getDrawable(id: Int): Drawable? =
        ContextCompat.getDrawable(binding.root.context, id)?.mutate()

    private fun TextView.setRightDrawable(drawable: Drawable?) {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}
package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTasksAreaBinding
import com.avcialper.owlcalendar.data.models.Date
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class TasksArea @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val layoutInflater = LayoutInflater.from(context)
    private val binding = ComponentTasksAreaBinding.inflate(layoutInflater, this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.TasksArea) {
            val emptyText = getString(R.styleable.TasksArea_empty_text)
            binding.componentTasks.changeEmptyText(emptyText)
        }
    }

    fun setTitle(date: StartDate) {
        val (year, month, day) = date
        binding.textTitle.text =
            String.format(Locale.getDefault(), "%02d.%02d.%04d", day, month + 1, year)
    }

    fun setTitle(titleId: Int) {
        binding.textTitle.text = context.getString(titleId)
    }

    fun setTitle(date: Date) {
        binding.textTitle.text = date.date
    }

    fun setOnSeeAllClickListener(listener: () -> Unit) {
        binding.header.setOnClickListener {
            listener.invoke()
        }
    }

    fun changeList(tasks: List<Task>) {
        binding.componentTasks.changeList(tasks)
    }

}
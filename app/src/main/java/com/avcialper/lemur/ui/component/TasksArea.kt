package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTasksAreaBinding
import com.avcialper.lemur.util.constant.FilterType
import com.avcialper.lemur.util.formatDate
import com.avcialper.owlcalendar.data.models.Date
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.AndroidEntryPoint

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
        binding.textTitle.text = formatDate(date)
    }

    fun setTitle(date: Date) {
        binding.textTitle.text = date.date
    }

    private fun setOnSeeAllClickListener(listener: () -> Unit) {
        binding.textTitle.setOnClickListener {
            listener.invoke()
        }
    }

    fun create(
        tasks: List<Task> = emptyList(),
        title: Int,
        filterType: FilterType = FilterType.ALL,
        filterDate: String? = null,
        onSeeAllClickListener: (filterType: FilterType, filterDate: String?) -> Unit
    ) {
        binding.textTitle.text = context.getString(title)
        binding.componentTasks.changeList(tasks)
        setOnSeeAllClickListener { onSeeAllClickListener(filterType, filterDate) }
    }

    fun handleLoading(isLoading: Boolean) {
        binding.componentTasks.handleLoading(isLoading)
    }

    fun handleSuccess(data: List<Task>?) {
        val tasks = data ?: emptyList()
        binding.componentTasks.changeList(tasks)
    }

}
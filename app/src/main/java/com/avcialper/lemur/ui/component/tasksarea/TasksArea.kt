package com.avcialper.lemur.ui.component.tasksarea

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.ComponentTasksAreaBinding
import com.avcialper.lemur.helper.NonScrollableLinerLayoutManager
import com.avcialper.owlcalendar.data.models.Date
import com.avcialper.owlcalendar.data.models.StartDate
import com.google.android.material.divider.MaterialDividerItemDecoration
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

    private val tasks: MutableList<Task> = mutableListOf()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TasksArea)

        val emptyText = a.getString(R.styleable.TasksArea_empty_text)
        with(binding) {
            textEmpty.text = emptyText
            rvTasks.visibility = GONE
        }

        a.recycle()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = TaskRecyclerView(tasks)
        val layoutManager = NonScrollableLinerLayoutManager(context)
        val itemDecorationWithoutLastItem = MaterialDividerItemDecoration(context, VERTICAL).apply {
            isLastItemDecorated = false
        }

        binding.rvTasks.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(itemDecorationWithoutLastItem)
        }
    }

    fun setTitle(date: StartDate) {
        val (year, month, day) = date
        val monthTitle = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayTitle = if (day < 10) "0$day" else "$day"
        binding.textTitle.text =
            String.format(Locale.getDefault(), "%s.%s.%d", dayTitle, monthTitle, year)
    }

    fun setTitle(titleId: Int) {
        binding.textTitle.text = context.getString(titleId)
    }

    fun setTitle(date: Date) {
        binding.textTitle.text = date.date
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)

        with(binding) {
            if (tasks.isEmpty()) {
                rvTasks.visibility = GONE
                textEmpty.visibility = VISIBLE
            } else {
                rvTasks.visibility = VISIBLE
                textEmpty.visibility = GONE
            }

            rvTasks.adapter?.notifyItemInserted(0)
        }
    }

    fun setOnSeeAllClickListener(listener: () -> Unit) {
        binding.header.setOnClickListener {
            listener.invoke()
        }
    }

}
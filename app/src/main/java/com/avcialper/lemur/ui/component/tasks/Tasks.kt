package com.avcialper.lemur.ui.component.tasks

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.databinding.ComponentTasksBinding
import com.avcialper.lemur.helper.NonScrollableLinerLayoutManager
import com.avcialper.lemur.ui.component.tasks.adapter.TasksAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration

class Tasks @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val layoutInflater = LayoutInflater.from(context)
    private val binding = ComponentTasksBinding.inflate(layoutInflater, this, true)

    private var onClick: (String) -> Unit = {}

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Tasks)
        val emptyText =
            a.getString(R.styleable.Tasks_empty_text) ?: context.getString(R.string.def_empty_task)
        val isShort = a.getBoolean(R.styleable.Tasks_is_short, false)
        val haveMargin = a.getBoolean(R.styleable.Tasks_have_margin, false)

        binding.apply {
            textEmpty.text = emptyText
            val margin =
                if (haveMargin) context.resources.getDimensionPixelSize(R.dimen.margin_large) else 0

            val layoutParams = root.layoutParams as? MarginLayoutParams
            layoutParams?.setMargins(margin, 0, margin, 0)
            root.layoutParams = layoutParams
        }

        a.recycle()

        initRecyclerView(isShort)
    }

    private fun initRecyclerView(isShort: Boolean) {
        val adapter = TasksAdapter(emptyList(), isShort) {
            onClick(it)
        }
        val layoutManager =
            if (isShort) NonScrollableLinerLayoutManager(context) else LinearLayoutManager(context)
        val itemDecoration = MaterialDividerItemDecoration(context, VERTICAL).apply {
            isLastItemDecorated = false
        }

        binding.rvTasks.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(itemDecoration)
        }
    }

    fun changeEmptyText(text: String?) {
        if (text == null) return    // if text is null, do nothing
        binding.textEmpty.text = text
    }

    fun changeList(tasks: List<TaskCard>) = with(binding) {
        val adapter = rvTasks.adapter as TasksAdapter
        adapter.changeList(tasks)
        if (tasks.isEmpty()) {
            rvTasks.visibility = GONE
            textEmpty.visibility = VISIBLE
        } else {
            rvTasks.visibility = VISIBLE
            textEmpty.visibility = GONE
        }
    }

    fun handleLoading(isLoading: Boolean) = with(binding) {
        if (isLoading)
            textEmpty.visibility = GONE
        skeleton.root.visibility = if (isLoading) VISIBLE else GONE
    }

    fun setOnTaskClickListener(onClick: (String) -> Unit) {
        this.onClick = onClick
    }

}
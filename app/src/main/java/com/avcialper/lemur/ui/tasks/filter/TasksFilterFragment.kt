package com.avcialper.lemur.ui.tasks.filter

import android.text.Editable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentTasksFilterBinding
import com.avcialper.lemur.helper.SimplifiedTextWatcher
import com.avcialper.lemur.helper.SmoothScroller
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.DateTimePicker
import com.avcialper.lemur.ui.tasks.filter.adapter.FilterAdapter
import com.avcialper.lemur.util.constant.DateTimePickerType
import com.avcialper.lemur.util.constant.FilterType
import com.avcialper.lemur.util.extension.toFixedString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFilterFragment :
    BaseFragment<FragmentTasksFilterBinding>(FragmentTasksFilterBinding::inflate) {

    private val vm: TasksFilterViewModel by viewModels()

    private val args: TasksFilterFragmentArgs by navArgs()

    private var filterType: FilterType? = null
    private var filterDate: String? = null

    override fun FragmentTasksFilterBinding.initialize() {
        filterType = args.filterType
        filterDate = args.filterDate
        initUI()
        observer()
        setListeners()
    }

    private fun initUI() {
        initFilterRecyclerView()
        handleFilterType()
    }

    private fun initFilterRecyclerView() {
        val adapter = FilterAdapter(filterType, filterDate, ::onFilterChangeListener)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilter.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            post {
                val scroller = SmoothScroller(context, filterType?.ordinal ?: 0)
                layoutManager.startSmoothScroll(scroller)
            }
        }
    }

    private fun onFilterChangeListener(type: FilterType, data: String?) {
        filterType = type
        if (type == FilterType.DATE)
            DateTimePicker(
                type = DateTimePickerType.DATE,
                date = data,
                onCompleted = ::onDateSelected,
            ).show(
                childFragmentManager,
                "date_selector"
            )
        else
            handleFilterType()
    }

    private fun onDateSelected(date: String) {
        val adapter = binding.rvFilter.adapter as FilterAdapter
        filterDate = date
        adapter.title = date
        adapter.notifyItemChanged(FilterType.DATE.ordinal)
        handleFilterType()
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(data: List<Task>?) {
        binding.componentTasks.changeList(data ?: emptyList())
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.componentTasks.handleLoading(isLoading)
    }

    private fun handleFilterType() {
        binding.componentTasks.changeList(emptyList())
        when (filterType) {
            FilterType.ALL -> vm.getAllTasks()
            FilterType.DATE -> vm.getTasksByDate(filterDate!!)
            FilterType.TODAY -> vm.getTodayTasks()
            FilterType.PERSONAL -> vm.getPersonalTasks()
            FilterType.TEAM -> vm.getTeamTasks()
            FilterType.MEET -> vm.getMeets()
            FilterType.CONTINUES -> vm.getContinuesTasks()
            FilterType.COMPLETED -> vm.getCompletedTasks()
            FilterType.CANCELED -> vm.getCanceledTasks()
            else -> Unit
        }
    }

    private fun setListeners() = with(binding) {
        searchBar.addTextChangedListener(object : SimplifiedTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filterText = s?.toFixedString()?.lowercase() ?: ""
                val tasks = vm.state.value.data ?: emptyList()
                val filteredTasks = tasks.filter { task ->
                    task.name.lowercase().contains(filterText)
                            || task.description.lowercase().contains(filterText)
                }

                val data = if (filterText.isNotEmpty())
                    filteredTasks
                else
                    tasks

                componentTasks.changeList(data)
            }
        })
    }

}
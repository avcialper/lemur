package com.avcialper.lemur.ui.tasks.filter

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.databinding.FragmentTasksFilterBinding
import com.avcialper.lemur.helper.SmoothScroller
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.DatePicker
import com.avcialper.lemur.util.constant.FilterType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFilterFragment :
    BaseFragment<FragmentTasksFilterBinding>(FragmentTasksFilterBinding::inflate) {

    private val vm: TasksFilterViewModel by viewModels()

    private val args: TasksFilterFragmentArgs by navArgs()

    override fun FragmentTasksFilterBinding.initialize() {
        if (vm.filterType == null)
            vm.filterType = args.filterType
        if (vm.filterDate == null)
            vm.filterDate = args.filterDate
        initUI()
    }

    private fun initUI() {
        initFilterRecyclerView()
    }

    private fun initFilterRecyclerView() {
        val adapter = FilterAdapter(vm.filterType, vm.filterDate, ::onFilterChangeListener)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilter.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            post {
                val scroller = SmoothScroller(context, vm.filterType?.ordinal ?: 0)
                layoutManager.startSmoothScroll(scroller)
            }
        }
    }

    private fun onFilterChangeListener(type: FilterType, data: String?) {
        vm.filterType = type
        if (type == FilterType.DATE)
            DatePicker(data, ::onDateSelected).show(childFragmentManager, "date_selector")
    }

    private fun onDateSelected(date: String) {
        val adapter = binding.rvFilter.adapter as FilterAdapter
        vm.filterDate = date
        adapter.title = date
        adapter.notifyItemChanged(FilterType.DATE.ordinal)
    }

}
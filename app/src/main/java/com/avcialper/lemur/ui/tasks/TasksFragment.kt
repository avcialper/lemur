package com.avcialper.lemur.ui.tasks

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.databinding.FragmentTasksBinding
import com.avcialper.lemur.helper.SmoothScroller
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.constant.FilterType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    private val vm: TasksViewModel by viewModels()

    private val args: TasksFragmentArgs by navArgs()

    override fun FragmentTasksBinding.initialize() {
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
        if (type == FilterType.DATE) {
            vm.filterDate = data
        }
    }

}
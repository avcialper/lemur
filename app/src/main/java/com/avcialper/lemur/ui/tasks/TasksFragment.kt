package com.avcialper.lemur.ui.tasks

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.databinding.FragmentTasksBinding
import com.avcialper.lemur.helper.SmoothScroller
import com.avcialper.lemur.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    private val vm: TasksViewModel by viewModels()

    private val args: TasksFragmentArgs by navArgs()

    override fun FragmentTasksBinding.initialize() {
        vm.filterType = args.filterType
        initUI()
    }

    private fun initUI() {
        initFilterRecyclerView()
    }

    private fun initFilterRecyclerView() {
        val adapter = FilterAdapter(vm.filterType) { type -> vm.filterType = type }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilter.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            post {
                val scroller = SmoothScroller(context).apply {
                    targetPosition = vm.filterType.ordinal
                }
                layoutManager.startSmoothScroll(scroller)
            }
        }
    }

}
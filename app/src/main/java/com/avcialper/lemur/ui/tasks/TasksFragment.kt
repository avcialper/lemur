package com.avcialper.lemur.ui.tasks

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.databinding.FragmentTasksBinding
import com.avcialper.lemur.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    private val vm: TasksViewModel by viewModels()

    override fun FragmentTasksBinding.initialize() {
        initUI()
    }

    private fun initUI() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = FilterAdapter(vm.filterType) { type -> vm.filterType = type }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilter.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
    }

}
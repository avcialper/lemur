package com.avcialper.lemur.ui.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avcialper.lemur.databinding.FragmentTaskTypeSheetBinding
import com.avcialper.lemur.util.constant.TaskType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskTypeSheet(
    private val onTypeSelected: (TaskType) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentTaskTypeSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskTypeSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            componentTask.setOnClickListener {
                onTypeSelected(TaskType.PERSONAL)
                dismiss()
            }

            componentMeet.setOnClickListener {
                onTypeSelected(TaskType.MEET)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
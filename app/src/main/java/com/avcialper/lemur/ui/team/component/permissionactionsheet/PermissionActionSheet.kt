package com.avcialper.lemur.ui.team.component.permissionactionsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.data.model.local.SelectablePermission
import com.avcialper.lemur.databinding.FragmentPermissionActionSheetBinding
import com.avcialper.lemur.ui.team.component.permissionactionsheet.adapter.PermissionSelectorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PermissionActionSheet(
    permissions: List<SelectablePermission>,
    private val onCompleted: (List<SelectablePermission>) -> Unit
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentPermissionActionSheetBinding? = null
    private val binding get() = _binding!!

    private val data = permissions.map { it.copy() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionActionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.buttonSelect.setOnClickListener {
            onCompleted(data)
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val adapter = PermissionSelectorAdapter(data) { index, isChecked ->
            data[index].isSelected = isChecked
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermissions.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
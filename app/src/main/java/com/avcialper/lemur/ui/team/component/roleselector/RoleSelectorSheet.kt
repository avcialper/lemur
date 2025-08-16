package com.avcialper.lemur.ui.team.component.roleselector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.RoleCard
import com.avcialper.lemur.databinding.FragmentRoleSelectorBinding
import com.avcialper.lemur.ui.team.component.roleselector.adapter.RoleSelectorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RoleSelectorSheet(
    roles: List<RoleCard>,
    private val onCompleted: (List<Role>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRoleSelectorBinding
    private val data = roles.map { it.copy() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoleSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        Log.e("ROLES", data.toString())

        binding.buttonSelect.setOnClickListener {
            val selectedRoles = data.filter { it.isSelected }.map { it.toRole() }
            onCompleted(selectedRoles)
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val adapter = RoleSelectorAdapter(data) { index, isChecked ->
            data[index].isSelected = isChecked
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoles.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
    }
}
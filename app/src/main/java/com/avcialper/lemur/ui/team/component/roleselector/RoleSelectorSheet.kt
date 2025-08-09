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
    private val roles: List<RoleCard>,
    private val onCompleted: (List<Role>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRoleSelectorBinding

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
        binding.buttonSelect.setOnClickListener {
            val selectedRoles = roles.filter { it.isSelected }.map { it.toRole() }
            onCompleted(selectedRoles)
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val adapter = RoleSelectorAdapter(roles) { index, isChecked ->
            roles[index].isSelected = isChecked
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoles.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
    }
}
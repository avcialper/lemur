package com.avcialper.lemur.ui.team.component.roleactionsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.databinding.FragmentRoleActionSheetBinding
import com.avcialper.lemur.util.constant.RoleBottomSheetActions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RoleActionSheet(
    private val role: Role,
    private val actionHandler: (RoleBottomSheetActions, Role, onSuccess: () -> Unit) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentRoleActionSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleActionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            textRoleName.text = role.name

            lwaOpenMembers.setOnClickListener {
                handleActions(RoleBottomSheetActions.MEMBERS)
            }

            lwaAssignRole.setOnClickListener {
                handleActions(RoleBottomSheetActions.ASSIGN_ROLE)
            }

            lwaUpdateRole.setOnClickListener {
                handleActions(RoleBottomSheetActions.UPDATE)
            }

            lwaDeleteRole.setOnClickListener {
                handleActions(RoleBottomSheetActions.DELETE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleActions(action: RoleBottomSheetActions) {
        actionHandler(action, role) {
            dismiss()
        }
    }

}
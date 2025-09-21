package com.avcialper.lemur.ui.team.roles.update

import android.content.res.ColorStateList
import android.view.ContextThemeWrapper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.SelectablePermission
import com.avcialper.lemur.databinding.FragmentUpdateRoleBinding
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.RequireLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.component.permissionactionsheet.PermissionActionSheet
import com.avcialper.lemur.util.constant.Permissions
import com.avcialper.lemur.util.extension.formatInvalidMinLengthError
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateRoleFragment :
    BaseFragment<FragmentUpdateRoleBinding>(FragmentUpdateRoleBinding::inflate) {

    private val args: UpdateRoleFragmentArgs by navArgs()
    private val vm: UpdateRoleViewModel by viewModels()

    private var permissions: List<SelectablePermission> = emptyList()

    override fun FragmentUpdateRoleBinding.initialize() {
        vm.getRole(args.teamId, args.roleCode)
        observer()
        initUI()
    }

    private fun initUI() = with(binding) {
        textChangePermissions.setOnClickListener {
            PermissionActionSheet(permissions) { updatePermissions ->
                permissions = updatePermissions

                val permissionNames = permissions.filter { permission ->
                    permission.isSelected
                }.map { permission ->
                    permission.permission.name
                }
                updatePermissionsChips(permissionNames)
            }.show(childFragmentManager, "permission_action_sheet")
        }

        buttonUpdate.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                val permissions = permissions.filter { permission ->
                    permission.isSelected
                }.map { permission ->
                    permission.permission.name
                }

                val role = Role(args.roleCode, componentRoleName.value, permissions)
                vm.updateRole(args.teamId, role)
            }
        }
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.updateStatus.createResourceObserver(::handleUpdateSuccess, ::handleLoading)
    }

    private fun handleSuccess(role: Role?) = with(binding) {

        if (role == null)
            return@with

        componentRoleName.value = role.name
        updatePermissionsChips(role.permissions)

        permissions = Permissions.entries.map { permission ->
            val isPermissionSelected = role.permissions.contains(permission.name)
            SelectablePermission(permission, isPermissionSelected)
        }

    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE

        val othersVisibility = if (isLoading) View.GONE else View.VISIBLE

        componentRoleName.visibility = othersVisibility
        textChangePermissions.visibility = othersVisibility
        cgPermissions.visibility = othersVisibility
        buttonUpdate.visibility = othersVisibility
    }

    private fun handleUpdateSuccess() {
        goBack()
    }

    private fun updatePermissionsChips(data: List<String>) = with(binding) {
        cgPermissions.removeAllViews()
        val myChipBackgroundColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.chardonnay
            )
        )

        data.forEach { permission ->
            val permissionId = Permissions.valueOf(permission).label
            val label = getString(permissionId)
            val chip =
                Chip(ContextThemeWrapper(root.context, R.style.SelectedRoleChip), null, 0).apply {
                    text = label
                    chipBackgroundColor = myChipBackgroundColor
                }
            cgPermissions.addView(chip)
        }
    }

    private fun validate(): Boolean {
        val minRoleNameLength = getInt(R.integer.min_role_name_length)
        val isValidRoleName = binding.componentRoleName.validate(
            rules = listOf(
                EmptyRule(),
                RequireLengthRule(minRoleNameLength)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidMinLengthError(
                    requireContext(),
                    R.string.role,
                    minRoleNameLength
                )
            }
        )

        return isValidRoleName
    }
}
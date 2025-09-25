package com.avcialper.lemur.ui.team.roles.create

import android.content.res.ColorStateList
import android.view.ContextThemeWrapper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.SelectablePermission
import com.avcialper.lemur.databinding.FragmentCreateRoleBinding
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.component.permissionactionsheet.PermissionActionSheet
import com.avcialper.lemur.util.constant.Permissions
import com.avcialper.lemur.util.extension.formatInvalidMinLengthError
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CreateRoleFragment :
    BaseFragment<FragmentCreateRoleBinding>(FragmentCreateRoleBinding::inflate) {

    private val args: CreateRoleFragmentArgs by navArgs()
    private val vm: CreateRoleViewModel by viewModels()

    private var permissions = Permissions.entries.map { permission ->
        SelectablePermission(permission, false)
    }

    override fun FragmentCreateRoleBinding.initialize() {
        observer()
        initUI()
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun initUI() = with(binding) {
        textSelectPermissions.setOnClickListener {
            PermissionActionSheet(permissions) { updatedPermissions ->
                permissions = updatedPermissions

                val permissionNames = permissions.filter { permission ->
                    permission.isSelected
                }.map { permission ->
                    permission.permission.name
                }

                updatePermissionsChips(permissionNames)
            }.show(childFragmentManager, "permission_action_sheet")
        }

        buttonCreate.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                val permissions = permissions.filter { permission ->
                    permission.isSelected
                }.map { permission ->
                    permission.permission.name
                }
                val roleCode = UUID.randomUUID().toString().uppercase().take(6)
                val role = Role(roleCode, componentRoleName.value, permissions)
                vm.createRole(args.teamId, role)
            }
        }

    }

    private fun handleSuccess() {
        goBack()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE

        val othersVisibility = if (isLoading) View.GONE else View.VISIBLE

        componentRoleName.visibility = othersVisibility
        textSelectPermissions.visibility = othersVisibility
        cgPermissions.visibility = othersVisibility
        buttonCreate.visibility = othersVisibility
    }

    private fun validate(): Boolean {
        val minRoleNameLength = getInt(R.integer.min_role_name_length)
        val isValidRoleName = binding.componentRoleName.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minRoleNameLength)
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
}
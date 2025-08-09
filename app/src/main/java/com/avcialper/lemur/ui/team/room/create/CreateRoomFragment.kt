package com.avcialper.lemur.ui.team.room.create

import android.content.res.ColorStateList
import android.view.ContextThemeWrapper
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.RoleCard
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.databinding.FragmentCreateRoomBinding
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.component.roleselector.RoleSelectorSheet
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CreateRoomFragment :
    BaseFragment<FragmentCreateRoomBinding>(FragmentCreateRoomBinding::inflate) {

    private val args: CreateRoomFragmentArgs by navArgs()
    private val vm: CreateRoomViewModel by viewModels()

    private var roles = emptyArray<RoleCard>()
    private var selectedRoles = emptyList<Role>()

    override fun FragmentCreateRoomBinding.initialize() {
        roles = args.roles.filter { it.code != "ADMIN" }.map { it.toRoleCard() }.toTypedArray()
        setListeners()
        observe()
    }

    private fun setListeners() = with(binding) {
        buttonCreateRoom.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                val uuid = UUID.randomUUID().toString()
                val room = Room(
                    uuid,
                    args.teamId,
                    inputRoomName.value,
                    inputRoomDescription.value,
                    selectedRoles.map { it.code }
                )
                vm.createRoom(room)
            }
        }

        cgRoles.setOnClickListener { openRoleSelector() }
        accessibleRolesLabel.setOnClickListener { openRoleSelector() }
    }

    private fun validate(): Boolean = with(binding) {
        val maxRoomNameLength = getInt(R.integer.max_room_name_length)
        val isValidRoomName = inputRoomName.validate(
            rules = listOf(
                EmptyRule(),
                MaxLengthRule(maxRoomNameLength)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.room_name,
                    maxRoomNameLength
                )
            }
        )

        val maxDescriptionLength = getInt(R.integer.max_room_description_length)
        val isValidDescription = inputRoomDescription.validate(
            rules = listOf(
                EmptyRule(),
                MaxLengthRule(maxDescriptionLength)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.description,
                    maxDescriptionLength
                )
            }
        )

        val isHaveSelectedRole = cgRoles.isNotEmpty()
        if (!isHaveSelectedRole) {
            toast(R.string.select_one_role)
            return false
        }

        return isValidRoomName && isValidDescription
    }

    private fun updateChipGroup(selectedRoles: List<Role>) {
        this.selectedRoles = selectedRoles
        binding.cgRoles.removeAllViews()
        selectedRoles.forEach { role ->
            roles.map {
                if (selectedRoles.contains(it.toRole())) {
                    it.isSelected = true
                }
            }

            val chip = Chip(ContextThemeWrapper(context, R.style.SelectedRoleChip), null, 0).apply {
                text = role.name
                chipBackgroundColor = ColorStateList.valueOf(getInt(R.color.chardonnay))
            }
            binding.cgRoles.addView(chip)
        }
    }

    private fun openRoleSelector() {
        RoleSelectorSheet(roles.toList(), ::updateChipGroup).show(
            childFragmentManager,
            "role_selector"
        )
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        inputRoomName.setLoadingState(isLoading)
        inputRoomDescription.setLoadingState(isLoading)
        buttonCreateRoom.updateLoadingState(isLoading)
        accessibleRolesLabel.updateLoadingState(isLoading)
        cgRoles.updateLoadingState(isLoading)
    }
}

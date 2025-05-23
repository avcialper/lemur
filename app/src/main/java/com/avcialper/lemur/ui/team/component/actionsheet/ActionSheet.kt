package com.avcialper.lemur.ui.team.component.actionsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.avcialper.lemur.databinding.FragmentActionSheetBinding
import com.avcialper.lemur.util.constant.TeamBottomSheetActions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionSheet(
    private val imageUrl: String?,
    private val teamName: String?,
    private val teamDescription: String?,
    private val onActionHandle: (TeamBottomSheetActions) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentActionSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            teamImage.load(imageUrl)
            tvTeamName.text = teamName
            tvTeamDescription.text = teamDescription

            actionUpdate.setOnClickListener {
                handleAction(TeamBottomSheetActions.UPDATE)
            }
            actionMembers.setOnClickListener {
                handleAction(TeamBottomSheetActions.MEMBERS)
            }
            actionInviteCode.setOnClickListener {
                handleAction(TeamBottomSheetActions.INVITE_CODE)
            }
            actionRoleManagement.setOnClickListener {
                handleAction(TeamBottomSheetActions.ROLE_MANAGEMENT)
            }
            actionLeaveTeam.setOnClickListener {
                handleAction(TeamBottomSheetActions.LEAVE_TEAM)
            }
        }
    }

    private fun handleAction(action: TeamBottomSheetActions) {
        onActionHandle(action)
        dismiss()
    }

}
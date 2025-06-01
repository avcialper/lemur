package com.avcialper.lemur.ui.team.component.actionsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.avcialper.lemur.databinding.FragmentActionSheetBinding
import com.avcialper.lemur.util.constant.TeamBottomSheetActions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionSheet(
    private val imageUrl: String?,
    private val teamName: String?,
    private val teamDescription: String?,
    private val onActionHandle: (TeamBottomSheetActions) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentActionSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true

            val layoutParams = it.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.layoutParams = layoutParams
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            if (imageUrl != null)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
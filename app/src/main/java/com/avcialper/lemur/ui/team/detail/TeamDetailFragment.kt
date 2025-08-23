package com.avcialper.lemur.ui.team.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.databinding.FragmentTeamDetailBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.team.component.actionsheet.ActionSheet
import com.avcialper.lemur.ui.team.component.leadselector.LeadSelectorFragment
import com.avcialper.lemur.ui.team.detail.adapter.RoomAdapter
import com.avcialper.lemur.util.constant.Permissions
import com.avcialper.lemur.util.constant.TeamBottomSheetActions
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamDetailFragment :
    BaseFragment<FragmentTeamDetailBinding>(FragmentTeamDetailBinding::inflate) {

    private val vm: TeamDetailViewModel by viewModels()
    private val args: TeamDetailFragmentArgs by navArgs()
    private var userPermissions: List<String> = emptyList()

    override fun FragmentTeamDetailBinding.initialize() {
        vm.getTeam(args.teamId)
        observe()
        setListeners()
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.roomState.createResourceObserver(::handleRoomSuccess, ::handleLoading)
    }

    private fun handleSuccess(team: Team?) = with(binding) {
        team?.let {
            if (it.imageUrl != null)
                teamImage.load(it.imageUrl)

            tvTeamName.text = it.name
            tvTeamDescription.text = it.description
            vm.getRooms(it.rooms)

            val userRoles = it.members.find { member ->
                member.id == UserManager.user!!.id
            }?.roleCodes ?: emptyList()

            it.roles.forEach { role ->
                if (userRoles.contains(role.code)) {
                    userPermissions = userPermissions + role.permissions
                }
            }
            userPermissions = userPermissions.distinct()

            val isHaveRoomAndTaskPermission =
                userPermissions.contains(Permissions.TASK_MANAGEMENT.name) &&
                        userPermissions.contains(Permissions.ROOM_MANAGEMENT.name)

            if (!isHaveRoomAndTaskPermission) {
                fab.visibility = View.GONE
                emptyArea.hideActionButton()
            }
        }
    }

    private fun handleRoomSuccess(rooms: List<Room>?) {
        val adapter = RoomAdapter(rooms ?: emptyList(), ::onRoomClick)
        val layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = false
        }

        binding.rooms.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(itemDecoration)
        }

        if (rooms.isNullOrEmpty()) {
            binding.emptyArea.visibility = View.VISIBLE
            binding.rooms.visibility = View.GONE
        } else {
            binding.emptyArea.visibility = View.GONE
            binding.rooms.visibility = View.VISIBLE
        }
    }

    private fun onRoomClick(roomId: String) {
        val direction = TeamDetailFragmentDirections.toRoomDetail()
        direction.navigate()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE

        val visibility = if (isLoading) View.GONE else View.VISIBLE
        teamImage.visibility = visibility
        tvTeamName.visibility = visibility
        tvTeamDescription.visibility = visibility
        divider.visibility = visibility
        rooms.visibility = visibility

        emptyArea.visibility = View.GONE

        if (isLoading) fab.hide() else fab.show()
    }

    private fun setListeners() = with(binding) {
        innerWrapper.setOnClickListener {
            openActionSheet()
            fab.close()
        }

        root.setOnClickListener {
            fab.close()
        }

        fab.setSecondFabClickListener {
            val roles = vm.state.value.data?.roles?.toTypedArray() ?: emptyArray()
            val direction = TeamDetailFragmentDirections.toCreateRoom(roles, args.teamId)
            direction.navigate()
        }

        emptyArea.setButtonAction {
            val roles = vm.state.value.data?.roles?.toTypedArray() ?: emptyArray()
            val direction = TeamDetailFragmentDirections.toCreateRoom(roles, args.teamId)
            direction.navigate()
        }
    }

    private fun openActionSheet() {
        val isOwner = vm.state.value.data?.teamLeadId == UserManager.user?.id
        ActionSheet(
            isOwner,
            userPermissions,
            ::bottomSheetActionHandler
        ).show(
            childFragmentManager,
            "action_sheet"
        )
    }

    private fun bottomSheetActionHandler(action: TeamBottomSheetActions, onSuccess: () -> Unit) {
        when (action) {
            TeamBottomSheetActions.UPDATE -> {
                val direction = TeamDetailFragmentDirections.toUpdateTeam(args.teamId)
                onSuccess()
                direction.navigate()
            }

            TeamBottomSheetActions.MEMBERS -> {
                val direction = TeamDetailFragmentDirections.toMembers(teamId = args.teamId)
                onSuccess()
                direction.navigate()
            }

            TeamBottomSheetActions.INVITE_CODE -> {
                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("invite_code", vm.state.value.data?.inviteCode)
                clipboard.setPrimaryClip(clip)
                toast(R.string.invite_code_copied)
            }

            TeamBottomSheetActions.ROLE_MANAGEMENT -> {
                val direction = TeamDetailFragmentDirections.toRoles()
                onSuccess()
                direction.navigate()
            }

            TeamBottomSheetActions.LEAVE_TEAM -> {

                val isOwner = vm.state.value.data?.teamLeadId == UserManager.user?.id

                if (isOwner)
                    LeadSelectorFragment(
                        args.teamId,
                        ::openActionSheet,
                        { leaveTeam(onSuccess) },
                        { deleteTeam(onSuccess) }
                    ).show(childFragmentManager, "lead_selector")
                else
                    AlertFragment(R.string.leave_team_question) {
                        leaveTeam(onSuccess)
                    }.show(childFragmentManager, "alert")
            }

            TeamBottomSheetActions.DELETE_TEAM -> {
                AlertFragment(R.string.delete_team_question) {
                    deleteTeam(onSuccess)
                }.show(childFragmentManager, "alert")
            }
        }
    }

    private fun leaveTeam(onSuccess: () -> Unit) {
        val member = vm.state.value.data!!.members.find { member ->
            member.id == UserManager.user!!.id
        }
        vm.leaveTeam(args.teamId, member!!) {
            onSuccess()
            goBack()
        }
    }

    private fun deleteTeam(onSuccess: () -> Unit) {
        val memberIDs = vm.state.value.data!!.members.map { member ->
            member.id
        }

        vm.deleteTeam(args.teamId, memberIDs) {
            onSuccess()
            goBack()
        }
    }
}
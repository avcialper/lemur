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
import com.avcialper.lemur.ui.team.detail.adapter.RoomAdapter
import com.avcialper.lemur.util.constant.TeamBottomSheetActions
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamDetailFragment :
    BaseFragment<FragmentTeamDetailBinding>(FragmentTeamDetailBinding::inflate) {

    private val vm: TeamDetailViewModel by viewModels()
    private val args: TeamDetailFragmentArgs by navArgs()

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
    }

    private fun onRoomClick(roomId: String) {
        val destination = TeamDetailFragmentDirections.toRoomDetail()
        destination.navigate()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE

        val visibility = if (isLoading) View.GONE else View.VISIBLE
        teamImage.visibility = visibility
        tvTeamName.visibility = visibility
        tvTeamDescription.visibility = visibility
        divider.visibility = visibility
        rooms.visibility = visibility

        if (isLoading) fab.hide() else fab.show()
    }

    private fun setListeners() = with(binding) {
        innerWrapper.setOnClickListener {
            val team = vm.state.value.data
            ActionSheet(
                team?.imageUrl,
                team?.name,
                team?.description,
                ::bottomSheetActionHandler
            ).show(
                childFragmentManager,
                "action_sheet"
            )
            fab.close()
        }
        root.setOnClickListener {
            fab.close()
        }
        fab.setSecondFabClickListener {
            val roles = vm.state.value.data?.roles?.toTypedArray() ?: emptyArray()
            val destination = TeamDetailFragmentDirections.toCreateRoom(roles, args.teamId)
            destination.navigate()
        }
    }

    private fun bottomSheetActionHandler(action: TeamBottomSheetActions) {
        when (action) {
            TeamBottomSheetActions.UPDATE -> {
                val destination = TeamDetailFragmentDirections.toUpdateTeam()
                destination.navigate()
            }

            TeamBottomSheetActions.MEMBERS -> {
                val destination = TeamDetailFragmentDirections.toMembers()
                destination.navigate()
            }

            TeamBottomSheetActions.INVITE_CODE -> {
                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("invite_code", vm.state.value.data?.inviteCode)
                clipboard.setPrimaryClip(clip)
                toast(R.string.invite_code_copied)
            }

            TeamBottomSheetActions.ROLE_MANAGEMENT -> {
                val destination = TeamDetailFragmentDirections.toRoles()
                destination.navigate()
            }

            TeamBottomSheetActions.LEAVE_TEAM -> {
                AlertFragment(R.string.leave_team_question, true) {
                    val member = vm.state.value.data!!.members.find { member ->
                        member.id == UserManager.user!!.id
                    }
                    vm.leaveTeam(args.teamId, member!!) {
                        goBack()
                    }
                }.show(childFragmentManager, "alert")
            }
        }
    }
}
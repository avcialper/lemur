package com.avcialper.lemur.ui.team.detail

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.databinding.FragmentTeamDetailBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.component.actionsheet.ActionSheet
import com.avcialper.lemur.util.constant.TeamBottomSheetActions
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
    }

    private fun handleSuccess(team: Team?) = with(binding) {
        team?.let {
            teamImage.load(it.imageUrl)
            tvTeamName.text = it.name
            tvTeamDescription.text = it.description
        }
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
        }
    }

    private fun bottomSheetActionHandler(action: TeamBottomSheetActions) {
        toast(action.name)
        when (action) {
            TeamBottomSheetActions.UPDATE -> {
                // TODO navigate to update team
            }

            TeamBottomSheetActions.MEMBERS -> {
                // TODO navigate to members
            }

            TeamBottomSheetActions.INVITE_CODE -> {
                // TODO copy invite code
            }

            TeamBottomSheetActions.ROLE_MANAGEMENT -> {
                // TODO navigate to role management
            }

            TeamBottomSheetActions.LEAVE_TEAM -> {
                // TODO leave team
            }
        }
    }
}
package com.avcialper.lemur.ui.team

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.databinding.FragmentTeamBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.adapter.TeamAdapter
import com.avcialper.lemur.ui.team.component.join.JoinTeamBottomSheet
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding>(FragmentTeamBinding::inflate) {

    private val vm: TeamViewModel by viewModels()

    override fun FragmentTeamBinding.initialize() {
        vm.getTeams()
        initUI()
        observe()
    }

    private fun initUI() = with(binding) {
        val teamAdapter = TeamAdapter(emptyList()) { teamId ->
            val destination = TeamFragmentDirections.toTeamDetail(teamId)
            destination.navigate()
        }
        val teamLayoutManager = LinearLayoutManager(requireContext())
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = false
        }
        rvTeams.apply {
            adapter = teamAdapter
            layoutManager = teamLayoutManager
            addItemDecoration(divider)
        }

        fab.apply {
            setFirstFabClickListener {
                val destination = TeamFragmentDirections.toCreateTeam()
                destination.navigate()
            }
            setSecondFabClickListener {
                close()
                JoinTeamBottomSheet {
                    vm.getTeams()
                }.show(childFragmentManager, "join_team_bottom_sheet")
            }
        }
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(teams: List<TeamCard>?) {
        teams?.let {
            (binding.rvTeams.adapter as TeamAdapter).changeList(it)
        }
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) {
            rvTeams.visibility = View.GONE
            fab.hide()
            View.VISIBLE
        } else {
            rvTeams.visibility = View.VISIBLE
            fab.show()
            View.GONE
        }
    }
}
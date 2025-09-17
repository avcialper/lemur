package com.avcialper.lemur.ui.team

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.databinding.FragmentTeamBinding
import com.avcialper.lemur.helper.Divider
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.adapter.TeamAdapter
import com.avcialper.lemur.ui.team.component.join.JoinTeamBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding>(FragmentTeamBinding::inflate) {

    private val vm: TeamViewModel by viewModels()

    override fun FragmentTeamBinding.initialize() {
        vm.getTeams()
        initUI()
        observe()
        setListeners()
    }

    private fun initUI() = with(binding) {
        val teamAdapter = TeamAdapter(emptyList()) { teamId ->
            val direction = TeamFragmentDirections.toTeamDetail(teamId)
            direction.navigate()
        }
        val teamLayoutManager = LinearLayoutManager(requireContext())
        val divider = Divider(requireContext())
        rvTeams.apply {
            adapter = teamAdapter
            layoutManager = teamLayoutManager
            addItemDecoration(divider)
        }
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(teams: List<TeamCard>?) = with(binding) {
        teams?.let {
            (rvTeams.adapter as TeamAdapter).changeList(it)

            if (it.isEmpty()) {
                emptyArea.visibility = View.VISIBLE
                rvTeams.visibility = View.GONE
            } else {
                emptyArea.visibility = View.GONE
                rvTeams.visibility = View.VISIBLE
            }
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

    private fun setListeners() = with(binding) {
        fab.apply {
            setFirstFabClickListener {
                val direction = TeamFragmentDirections.toCreateTeam()
                direction.navigate()
            }
            setSecondFabClickListener {
                close()
                JoinTeamBottomSheet {
                    vm.getTeams()
                }.show(childFragmentManager, "join_team_bottom_sheet")
            }
        }

        emptyArea.setButtonAction {
            fab.showChild()
        }

        rvTeams.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 10 && fab.isVisible())
                    fab.hide()
                else if (dy < -10 && fab.isGone())
                    fab.show()
            }
        })
    }
}
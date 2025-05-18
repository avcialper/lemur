package com.avcialper.lemur.ui.team

import com.avcialper.lemur.databinding.FragmentTeamBinding
import com.avcialper.lemur.ui.BaseFragment

class TeamFragment : BaseFragment<FragmentTeamBinding>(FragmentTeamBinding::inflate) {

    override fun FragmentTeamBinding.initialize() {
        initUI()
    }

    private fun initUI() = with(binding) {
        fab.apply {
            setFirstFabClickListener {
                val destination = TeamFragmentDirections.toCreateTeam()
                destination.navigate()
            }
            setSecondFabClickListener {
                // Open JoinTeamAlertDialog
            }
        }
    }
}
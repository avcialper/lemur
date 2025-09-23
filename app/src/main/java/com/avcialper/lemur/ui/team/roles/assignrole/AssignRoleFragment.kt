package com.avcialper.lemur.ui.team.roles.assignrole

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.FragmentAssignRoleBinding
import com.avcialper.lemur.helper.Divider
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.team.roles.assignrole.adapter.assign.AssignRoleAdapter
import com.avcialper.lemur.ui.team.roles.assignrole.adapter.selected.SelectedMemberAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssignRoleFragment :
    BaseFragment<FragmentAssignRoleBinding>(FragmentAssignRoleBinding::inflate) {

    private val vm: AssignRoleViewModel by viewModels()
    private val args: AssignRoleFragmentArgs by navArgs()

    private var members = emptyList<SelectableMemberCard>()
    private var searchText = ""

    override fun FragmentAssignRoleBinding.initialize() {
        vm.getMembers(args.teamId, args.role.code)
        observer()
        initUI()
    }

    private fun initUI() = with(binding) {
        textRoleName.text = args.role.name
        buttonConfirm.isEnabled = false

        val adapter = AssignRoleAdapter(members) { isSelected, memberId ->
            handleSelectionAction(memberId, isSelected)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        val divider = Divider(requireContext())

        rvMember.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)
        }

        val selectedMembersAdapter = SelectedMemberAdapter(emptyList()) { memberId ->
            handleSelectionAction(memberId)
        }
        val selectedMembersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        rvSelectedMembers.apply {
            this.adapter = selectedMembersAdapter
            this.layoutManager = selectedMembersLayoutManager
        }

        searchBar.addSearchTextChangedListener { searchValue ->
            searchText = searchValue
            filterMembers()
        }

        buttonConfirm.setOnClickListener {

            val questionLabel =
                getString(R.string.assign_role_question).replace("{0}", args.role.name)

            AlertFragment(stringLabel = questionLabel) {
                val memberIds = members.filter { member ->
                    member.isSelected
                }.map { member ->
                    member.id
                }

                vm.assignRoleToMembers(args.teamId, memberIds, args.role.code)
            }.show(childFragmentManager, "alert_dialog")
        }

    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.updateState.createResourceObserver(::handleUpdateSuccess, ::handleLoading)
    }

    private fun handleSuccess(data: List<SelectableMemberCard>?) {
        members = data ?: emptyList()
        changeMemberDataChange(members)
    }

    private fun handleUpdateSuccess() {
        goBack()
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun changeMemberDataChange(data: List<SelectableMemberCard>) = with(binding) {
        (rvMember.adapter as AssignRoleAdapter).changeData(data)
        emptyState.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE

        val selectedMembers = members.filter { member ->
            member.isSelected
        }
        if (selectedMembers.isEmpty()) {
            buttonConfirm.isEnabled = false
            buttonConfirm.alpha = 0.5f
            rvSelectedMembers.visibility = View.GONE
        } else {
            buttonConfirm.isEnabled = true
            buttonConfirm.alpha = 1f
            rvSelectedMembers.visibility = View.VISIBLE
            (rvSelectedMembers.adapter as SelectedMemberAdapter).changeData(selectedMembers)
        }
    }

    private fun handleSelectionAction(memberId: String, isSelected: Boolean = false) {
        val updatedMembers = members.map { member ->
            if (member.id == memberId)
                member.copy(isSelected = isSelected)
            else
                member
        }
        members = updatedMembers
        filterMembers()
    }

    private fun filterMembers() {
        val filteredMembers = members.filter { member ->
            member.name.contains(searchText, true)
        }

        val data = if (searchText.isNotEmpty())
            filteredMembers
        else
            members

        changeMemberDataChange(data)
    }

}
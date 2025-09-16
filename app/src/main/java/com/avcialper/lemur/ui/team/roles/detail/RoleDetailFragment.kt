package com.avcialper.lemur.ui.team.roles.detail

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.FragmentRoleDetailBinding
import com.avcialper.lemur.helper.Divider
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.team.roles.detail.adapter.RoleDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoleDetailFragment :
    BaseFragment<FragmentRoleDetailBinding>(FragmentRoleDetailBinding::inflate) {

    private val vm: RoleDetailViewModel by viewModels()
    private val args: RoleDetailFragmentArgs by navArgs()

    override fun FragmentRoleDetailBinding.initialize() {
        vm.getMembersByRole(args.teamId, args.role.code)
        observer()
        initUI()
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun initUI() = with(binding) {
        val adapter = RoleDetailAdapter(emptyList(), args.teamLeadId, ::removeRoleFromMember)
        val layoutManager = LinearLayoutManager(requireContext())
        val divider = Divider(requireContext())

        rvMember.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)
        }

        searchBar.addSearchTextChangedListener { searchText ->
            val members = vm.state.value.data ?: emptyList()
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

    private fun handleSuccess(data: List<MemberCard>?) {
        val fixedData = data ?: emptyList()
        changeMemberDataChange(fixedData)

        // TODO(add user role, create role, delete role)
    }

    private fun handleLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun changeMemberDataChange(data: List<MemberCard>) {
        (binding.rvMember.adapter as RoleDetailAdapter).changeData(data)
        binding.emptyState.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun removeRoleFromMember(member: MemberCard) {
        val label = requireContext().resources.getString(R.string.remove_role_from_member_message)
            .replace("{0}", member.name).replace("{1}", args.role.name)

        AlertFragment(stringLabel = label) {
            vm.removeRoleFromMember(args.teamId, member.id, args.role.code)
        }.show(childFragmentManager, "alert")
    }
}
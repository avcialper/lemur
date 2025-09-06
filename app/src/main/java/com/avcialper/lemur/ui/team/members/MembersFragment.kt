package com.avcialper.lemur.ui.team.members

import android.text.Editable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.FragmentMembersBinding
import com.avcialper.lemur.helper.SimplifiedTextWatcher
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.team.members.adapter.MemberAdapter
import com.avcialper.lemur.util.extension.toFixedString
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : BaseFragment<FragmentMembersBinding>(FragmentMembersBinding::inflate) {

    private val vm: MembersViewModel by viewModels()
    private val args: MembersFragmentArgs by navArgs()

    override fun FragmentMembersBinding.initialize() {
        vm.getMembers(args.teamId)
        observe()
        initUI()
        initListeners()
    }

    private fun initUI() = with(binding) {
        val adapter = MemberAdapter(emptyList(), ::removeMember, args.leadId)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = false
        }

        rvMembers.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)
        }

    }

    private fun initListeners() = with(binding) {
        searchBar.addTextChangedListener(object : SimplifiedTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filterText = s?.toFixedString() ?: ""
                val members = vm.state.value.data ?: emptyList()
                val filteredMembers = members.filter { member ->
                    member.name.lowercase().contains(filterText.lowercase())
                }

                val data = if (filterText.isNotEmpty())
                    filteredMembers
                else
                    members

                changeMemberAdapterData(data)
            }
        })
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(members: List<MemberCard>?) {
        val data = members ?: emptyList()
        changeMemberAdapterData(data)
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        if (isLoading) {
            rvMembers.visibility = View.GONE
            progress.visibility = View.VISIBLE
        } else {
            rvMembers.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }
    }

    private fun removeMember(member: MemberCard) {
        val label = requireContext().resources.getString(R.string.remove_member_message)
            .replace("{0}", member.name)

        AlertFragment(stringLabel = label) {
            vm.removeMember(args.teamId, member.toMember())
        }.show(childFragmentManager, "alert")
    }

    private fun changeMemberAdapterData(members: List<MemberCard>) = with(binding) {
        (rvMembers.adapter as MemberAdapter).changeList(members)

        emptyUser.visibility = if (members.isEmpty()) View.VISIBLE else View.GONE
    }
}
package com.avcialper.lemur.ui.team.roles

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.databinding.FragmentRolesBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.roles.adapter.RolesAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RolesFragment : BaseFragment<FragmentRolesBinding>(FragmentRolesBinding::inflate) {

    private val vm: RoleViewModel by viewModels()
    private val args: RolesFragmentArgs by navArgs()

    override fun FragmentRolesBinding.initialize() {
        vm.getRoles(args.teamId)
        initUI()
        observer()
    }

    private fun initUI() = with(binding) {
        val adapter = RolesAdapter(emptyList())
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = false
        }

        rvRoles.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)
        }

        searchBar.addSearchTextChangedListener { searchedText ->
            val roles = vm.state.value.data ?: emptyList()
            val filteredRoles = roles.filter { role ->
                role.name.contains(searchedText, true)
            }

            val data = if (searchedText.isNotEmpty())
                filteredRoles
            else
                roles

            changeRoleAdapterData(data)
        }
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(roles: List<Role>?) = with(binding) {
        val data = roles ?: emptyList()
        changeRoleAdapterData(data)
    }

    private fun handleLoading(loading: Boolean) = with(binding) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun changeRoleAdapterData(data: List<Role>) = with(binding) {
        (rvRoles.adapter as RolesAdapter).changeData(data)

        emptyRole.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }
}
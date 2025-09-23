package com.avcialper.lemur.ui.team.roles

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.databinding.FragmentRolesBinding
import com.avcialper.lemur.helper.Divider
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.team.component.roleactionsheet.RoleActionSheet
import com.avcialper.lemur.ui.team.roles.adapter.RolesAdapter
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.RoleBottomSheetActions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RolesFragment : BaseFragment<FragmentRolesBinding>(FragmentRolesBinding::inflate) {

    private val vm: RoleViewModel by viewModels()
    private val args: RolesFragmentArgs by navArgs()

    override fun FragmentRolesBinding.initialize() {
        vm.getRoles(args.teamId)
        vm.checkUserHaveRoleManagementPermission(args.teamId, UserManager.user!!.id)
        initUI()
        observer()
    }

    private fun initUI() = with(binding) {
        val adapter = RolesAdapter(emptyList()) { role ->

            val isHaveRoleManagementPermission = vm.isUserHaveRoleManagementPermission.value.data

            if (isHaveRoleManagementPermission == true && role.code != Constants.LEAD)
                RoleActionSheet(role, ::handleRoleActions).show(
                    childFragmentManager,
                    "role_action_sheet"
                )
            else {
                val direction =
                    RolesFragmentDirections.toRoleDetail(args.teamId, role, args.teamLeadId)
                direction.navigate()
            }
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = Divider(requireContext())

        rvRoles.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10 && fabCreateRole.isShown)
                        fabCreateRole.hide()
                    else if (dy < -10 && !fabCreateRole.isShown)
                        fabCreateRole.show()
                }
            })
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
        vm.isUserHaveRoleManagementPermission.createResourceObserverWithoutLoadingState(::handleUserPermissionSuccess)
    }

    private fun handleSuccess(roles: List<Role>?) {
        val data = roles ?: emptyList()
        changeRoleAdapterData(data)
    }

    private fun handleLoading(loading: Boolean) = with(binding) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun handleUserPermissionSuccess(isHaveRoleManagementPermission: Boolean?) {
        binding.fabCreateRole.visibility =
            if (isHaveRoleManagementPermission == true)
                View.VISIBLE
            else
                View.GONE
    }

    private fun changeRoleAdapterData(data: List<Role>) = with(binding) {
        (rvRoles.adapter as RolesAdapter).changeData(data)

        emptyRole.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun handleRoleActions(
        actionType: RoleBottomSheetActions,
        role: Role,
        onSuccess: () -> Unit
    ) {
        when (actionType) {
            RoleBottomSheetActions.MEMBERS -> {
                val direction =
                    RolesFragmentDirections.toRoleDetail(args.teamId, role, args.teamLeadId)
                onSuccess()
                direction.navigate()
            }

            RoleBottomSheetActions.ASSIGN_ROLE -> {
                val direction = RolesFragmentDirections.toAssignRole(args.teamId, role)
                onSuccess()
                direction.navigate()
            }

            RoleBottomSheetActions.UPDATE -> {
                val direction = RolesFragmentDirections.toRoleUpdate(args.teamId, role.code)
                onSuccess()
                direction.navigate()
            }

            RoleBottomSheetActions.DELETE -> {
                toast("handle_delete_role")
            }
        }
    }
}
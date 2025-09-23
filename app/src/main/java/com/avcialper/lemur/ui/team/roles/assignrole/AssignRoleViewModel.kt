package com.avcialper.lemur.ui.team.roles.assignrole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.data.repository.storage.role.RoleRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignRoleViewModel @Inject constructor(private val roleRepository: RoleRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<List<SelectableMemberCard>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Boolean>?>(null)
    val updateState = _updateState.asStateFlow()

    fun getMembers(teamId: String, roleCode: String) = viewModelScope.launch {
        roleRepository.getMembersNotInRole(teamId, roleCode).collect { resource ->
            _state.update { resource }
        }
    }

    fun assignRoleToMembers(teamId: String, memberIds: List<String>, roleCode: String) =
        viewModelScope.launch {
            roleRepository.assignRoleToMembers(teamId, memberIds, roleCode).collect { resource ->
                _updateState.update { resource }
            }
        }

}
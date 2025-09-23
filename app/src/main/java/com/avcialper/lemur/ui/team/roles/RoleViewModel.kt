package com.avcialper.lemur.ui.team.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.repository.storage.role.RoleRepository
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val roleRepository: RoleRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Role>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _isUserHaveRoleManagementPermission =
        MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val isUserHaveRoleManagementPermission = _isUserHaveRoleManagementPermission.asStateFlow()

    fun getRoles(teamId: String) = viewModelScope.launch {
        roleRepository.getRoles(teamId).collect { resource ->
            _state.update { resource }
        }
    }

    fun checkUserHaveRoleManagementPermission(teamId: String, userId: String) =
        viewModelScope.launch {
            teamRepository.isUserHaveRoleManagementPermission(teamId, userId)
                .collect { resource ->
                    _isUserHaveRoleManagementPermission.update { resource }
                }
        }

    fun deleteRole(teamId: String, roleCode: String) = viewModelScope.launch {
        _state.update { Resource.Loading() }
        roleRepository.deleteRole(teamId, roleCode).collect { resource ->
            if (resource is Resource.Success)
                getRoles(teamId)
            else if (resource is Resource.Error)
                _state.update { Resource.Error(resource.throwable) }
        }
    }
}
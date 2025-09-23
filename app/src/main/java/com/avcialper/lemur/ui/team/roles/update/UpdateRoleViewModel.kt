package com.avcialper.lemur.ui.team.roles.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.repository.storage.role.RoleRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateRoleViewModel @Inject constructor(private val roleRepository: RoleRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<Role>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _updateStatus = MutableStateFlow<Resource<Boolean>?>(null)
    val updateStatus = _updateStatus.asStateFlow()

    fun getRole(teamId: String, roleCode: String) = viewModelScope.launch {
        roleRepository.getRole(teamId, roleCode).collect { resource ->
            _state.update { resource }
        }
    }

    fun updateRole(teamId: String, role: Role) = viewModelScope.launch {
        roleRepository.updateRole(teamId, role).collect { resource ->
            _updateStatus.update { resource }
        }
    }

}
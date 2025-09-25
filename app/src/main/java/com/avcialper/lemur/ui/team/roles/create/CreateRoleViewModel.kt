package com.avcialper.lemur.ui.team.roles.create

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
class CreateRoleViewModel @Inject constructor(private val roleRepository: RoleRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    fun createRole(teamId: String, role: Role) = viewModelScope.launch {
        roleRepository.createRole(teamId, role).collect { resource ->
            _state.update { resource }
        }
    }

}
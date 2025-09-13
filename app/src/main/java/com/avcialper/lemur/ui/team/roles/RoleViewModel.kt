package com.avcialper.lemur.ui.team.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(private val repository: StorageRepository) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Role>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _isUserHaveRoleManagementPermission =
        MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val isUserHaveRoleManagementPermission = _isUserHaveRoleManagementPermission.asStateFlow()

    fun getRoles(teamId: String) = viewModelScope.launch {
        repository.getRoles(teamId).collect { resource ->
            _state.update { resource }
        }
    }

    fun checkUserHaveRoleManagementPermission(teamId: String, userId: String) = viewModelScope.launch {
        repository.isUserHaveRoleManagementPermission(teamId, userId).collect { resource ->
            _isUserHaveRoleManagementPermission.update { resource }
        }
    }

}
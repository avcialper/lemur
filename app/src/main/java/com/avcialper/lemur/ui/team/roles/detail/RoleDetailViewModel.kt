package com.avcialper.lemur.ui.team.roles.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleDetailViewModel @Inject constructor(private val storageRepository: StorageRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<List<MemberCard>>>(Resource.Loading())
    val state = _state

    fun getMembersByRole(teamId: String, roleCode: String) = viewModelScope.launch {
        storageRepository.getMembersByRole(teamId, roleCode).collect { resource ->
            _state.update { resource }
        }
    }

    fun removeRoleFromMember(teamId: String, memberId: String, roleCode: String) =
        viewModelScope.launch {
            _state.update { Resource.Loading() }
            storageRepository.removeRoleFromMember(teamId, memberId, roleCode).collect { resource ->
                if (resource is Resource.Success)
                    getMembersByRole(teamId, roleCode)
                else if (resource is Resource.Error)
                    _state.update { Resource.Error(resource.throwable) }
            }
        }
}
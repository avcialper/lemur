package com.avcialper.lemur.ui.team.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel
@Inject constructor(private val teamRepository: TeamRepository) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<MemberCard>>>(Resource.Loading())
    val state = _state.asStateFlow()

    fun getMembers(teamId: String) = viewModelScope.launch {
        teamRepository.getMembers(teamId).collect { resource ->
            _state.update { resource }
        }
    }

    fun removeMember(teamId: String, member: Member) = viewModelScope.launch {
        _state.update { Resource.Loading() }
        teamRepository.removeMemberFromTeam(teamId, member).collect { resource ->
            if (resource is Resource.Success)
                getMembers(teamId)
            else if (resource is Resource.Error)
                _state.update { Resource.Error(resource.throwable) }
        }
    }

}
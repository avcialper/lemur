package com.avcialper.lemur.ui.team.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.repository.storage.room.RoomRepository
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Team>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _roomState = MutableStateFlow<Resource<List<Room>>>(Resource.Loading())
    val roomState = _roomState.asStateFlow()

    fun getTeam(teamId: String) = viewModelScope.launch {
        teamRepository.getTeam(teamId).collect { resource ->
            _state.update { resource }
        }
    }

    fun getRooms(rooms: List<String>) = viewModelScope.launch {
        roomRepository.getRooms(rooms).collect { resource ->
            _roomState.update { resource }
        }
    }

    fun leaveTeam(teamId: String, member: Member, onSuccess: () -> Unit) = viewModelScope.launch {
        teamRepository.leaveTeam(teamId, member).collect { resource ->
            if (resource is Resource.Success) {
                onSuccess()
            } else if (resource is Resource.Error) {
                _state.update { Resource.Error(resource.throwable) }
            }
        }
    }

    fun deleteTeam(teamId: String, memberIDs: List<String>, onSuccess: () -> Unit) =
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId, memberIDs).collect { resource ->
                if (resource is Resource.Success) {
                    onSuccess()
                } else if (resource is Resource.Error) {
                    _state.update { Resource.Error(resource.throwable) }
                }
            }
        }

}
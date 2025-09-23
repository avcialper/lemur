package com.avcialper.lemur.ui.team.component.leadselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadSelectorViewModel @Inject constructor(private val teamRepository: TeamRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<List<SelectableMemberCard>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _changeState = MutableStateFlow<Resource<Boolean>?>(null)
    val changeState = _changeState.asStateFlow()

    fun getMembers(teamId: String) = viewModelScope.launch {
        teamRepository.getMembers(teamId).collect { resource ->
            when (resource) {
                is Resource.Error -> _state.update { Resource.Error(resource.throwable) }
                is Resource.Loading -> _state.update { Resource.Loading() }
                is Resource.Success -> _state.update {
                    Resource.Success(resource.data?.map { memberCard ->
                        memberCard.toSelectableMemberCard()
                    })
                }
            }
        }
    }

    fun changeTeamLead(teamId: String, newLeadId: String) = viewModelScope.launch {
        teamRepository.changeTeamLead(teamId, newLeadId).collect { resource ->
            _changeState.update { resource }
        }
    }

}
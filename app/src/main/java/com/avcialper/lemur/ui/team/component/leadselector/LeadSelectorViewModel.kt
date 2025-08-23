package com.avcialper.lemur.ui.team.component.leadselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadSelectorViewModel @Inject constructor(
    private val repository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<SelectableMemberCard>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _changeState = MutableStateFlow<Resource<Boolean>?>(null)
    val changeState = _changeState.asStateFlow()

    fun getMembers(teamId: String) = viewModelScope.launch {
        repository.getMembers(teamId).collect { resource ->
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

    fun changeTeamOwner(teamId: String, newOwnerId: String) = viewModelScope.launch {
        repository.changeTeamOwner(teamId, newOwnerId).collect { resource ->
            _changeState.update { resource }
        }
    }

}
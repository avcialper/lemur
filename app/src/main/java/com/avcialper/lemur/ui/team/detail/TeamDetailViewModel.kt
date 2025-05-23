package com.avcialper.lemur.ui.team.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Team>>(Resource.Loading())
    val state = _state.asStateFlow()

    fun getTeam(teamId: String) = viewModelScope.launch {
        storageRepository.getTeam(teamId).collect { resource ->
            _state.update { resource }
        }
    }

}
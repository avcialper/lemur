package com.avcialper.lemur.ui.team.component.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinTeamViewModel @Inject constructor(private val teamRepository: TeamRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    fun joinTeam(inviteCode: String) = viewModelScope.launch {
        val userId = UserManager.user!!.id
        teamRepository.joinTeam(inviteCode, userId).collect { resource ->
            _state.update { resource }
        }
    }

}
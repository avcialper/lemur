package com.avcialper.lemur.ui.team.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateTeamViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val imageUrl = MutableStateFlow<String?>(null)

    fun createTeam(name: String, description: String, file: File?) = viewModelScope.launch {
        if (file != null) {
            uploadImage(file)
        }
        addTeam(name, description)
    }

    private suspend fun uploadImage(file: File) {
        _state.update { Resource.Loading() }
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                imageUrl.value = resource.data?.data?.url
            else if (resource is Resource.Error)
                _state.value = Resource.Error(resource.throwable!!)
        }
    }

    private suspend fun addTeam(name: String, description: String) {
        val teamId = UUID.randomUUID().toString()
        val userId = UserManager.user!!.id
        val adminRole = Role("ADMIN", "Admin")
        val memberRole = Role("MEMBER", "Member")
        val member = Member(userId, adminRole.code)

        val team = Team(
            teamId,
            UserManager.user!!.id,
            name,
            description,
            imageUrl.value,
            listOf(member),
            listOf(adminRole, memberRole),
            teamId.take(8).uppercase()
        )

        storageRepository.createTeam(team).collect { resource ->
            if (resource is Resource.Success)
                storageRepository.addTeamToUser(userId, teamId).collect {
                    _state.update { resource }
                }
            else
                _state.update { resource }
        }
    }
}
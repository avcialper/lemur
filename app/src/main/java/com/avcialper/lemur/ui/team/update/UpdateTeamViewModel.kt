package com.avcialper.lemur.ui.team.update

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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateTeamViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Team>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Boolean>?>(null)
    val updateState = _updateState.asStateFlow()

    private val newImageUrl = MutableStateFlow<String?>(null)

    fun getTeamDetails(teamId: String) = viewModelScope.launch {
        storageRepository.getTeam(teamId).collect { resource ->
            _state.update { resource }
        }
    }

    fun updateTeam(
        teamId: String,
        name: String,
        description: String,
        imageUrl: String?,
        file: File?
    ) = viewModelScope.launch {
        file?.let {
            uploadImage(it)
        }
        update(teamId, name, description, imageUrl)
    }

    private suspend fun uploadImage(file: File) {
        _updateState.update { Resource.Loading() }
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                newImageUrl.value = resource.data?.data?.url
            else if (resource is Resource.Error)
                _updateState.value = Resource.Error(resource.throwable!!)
        }
    }

    private suspend fun update(
        teamId: String,
        name: String,
        description: String,
        imageUrl: String?
    ) {
        val url = newImageUrl.value ?: imageUrl
        storageRepository.updateTeam(teamId, url, name, description).collect { resource ->
            _updateState.update { resource }
        }
    }

}
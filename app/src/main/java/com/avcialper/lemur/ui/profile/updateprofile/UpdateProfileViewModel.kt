package com.avcialper.lemur.ui.profile.updateprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.repository.storage.user.UserRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val newImageUrl = MutableStateFlow<String?>(null)

    fun update(username: String, about: String, imageUrl: String?, file: File?) =
        viewModelScope.launch {
            if (AppManager.isConnected.not()) {
                _state.update { null }
                return@launch
            }

            file?.let {
                uploadImage(it)
            }
            updateUser(username, about, imageUrl)
        }

    private suspend fun uploadImage(file: File) {
        _state.update { Resource.Loading() }
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                newImageUrl.value = resource.data?.data?.url
            else if (resource is Resource.Error)
                _state.value = Resource.Error(resource.throwable!!)
        }
    }

    private suspend fun updateUser(username: String, about: String, imageUrl: String?) {
        val user = UserProfile(
            UserManager.user?.id!!,
            username,
            about,
            newImageUrl.value ?: imageUrl,
            UserManager.user?.teams
        )
        userRepository.updateUser(user).collect { resource ->
            _state.value = resource
        }
    }

}
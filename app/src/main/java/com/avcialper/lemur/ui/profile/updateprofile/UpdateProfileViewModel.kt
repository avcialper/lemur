package com.avcialper.lemur.ui.profile.updateprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.UserProfile
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val _imageUrl = MutableStateFlow(UserManager.user?.imageUrl)
    val imageUrl = _imageUrl.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _username = MutableStateFlow(UserManager.user?.username)
    val username = _username.asStateFlow()

    private val _about = MutableStateFlow(UserManager.user?.about)
    val email = _about.asStateFlow()

    fun onUsernameChanged(username: String) {
        _username.value = username
    }

    fun onAboutChanged(about: String) {
        _about.value = about
    }

    fun onImageChanged(uri: Uri) {
        _imageUri.value = uri
    }

    fun deleteImage() {
        _imageUri.value = null
        _imageUrl.value = null
    }

    fun update(convert: () -> File) = viewModelScope.launch {
        _state.value = Resource.Loading()
        if (_imageUri.value != null) {
            val file = convert()
            uploadImage(file)
        }
        updateUser()

    }

    private suspend fun uploadImage(file: File) {
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                _imageUrl.value = resource.data?.data?.url
            else if (resource is Resource.Error)
                _state.value = Resource.Error(resource.throwable!!)
        }
    }

    private suspend fun updateUser() {
        val user = UserProfile(
            UserManager.user?.id!!,
            _username.value!!,
            _about.value,
            _imageUrl.value
        )
        storageRepository.updateUser(user).collect { resource ->
            _state.value = resource
        }
    }

}
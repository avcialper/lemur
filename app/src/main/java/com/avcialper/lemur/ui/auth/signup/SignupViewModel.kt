package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.remote.ImgBBData
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.auth.AuthRepository
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
class SignupViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val _imageBB = MutableStateFlow<ImgBBData?>(null)

    // Handle signup click action
    fun onSignupClicked(
        username: String,
        email: String,
        password: String,
        imageUri: Uri?,
        convert: () -> File
    ) = viewModelScope.launch {
        if (AppManager.isConnected.not()) {
            _state.value = null
            return@launch
        }

        _state.update { Resource.Loading() }
        auth.signup(email, password).collect { resource ->
            when (resource) {
                is Resource.Error -> handleError(resource.throwable!!)
                is Resource.Loading -> _state.update { Resource.Loading() }
                is Resource.Success -> {
                    if (imageUri != null) {
                        uploadImage(convert)
                        val id = resource.data?.uid!!
                        createUser(id, username)
                    } else
                        createUser(resource.data?.uid!!, username)
                }
            }
        }
    }

    // Upload image to ImgBB
    private suspend fun uploadImage(convert: () -> File) {
        val file = convert()
        _state.value = Resource.Loading()
        storageRepository.uploadImage(file).collect { resource ->
            when (resource) {
                is Resource.Success -> _imageBB.update { resource.data?.data }
                is Resource.Loading -> _state.update { Resource.Loading() }
                is Resource.Error -> handleError(resource.throwable!!)
            }
        }
    }

    // Create user in Firebase storage
    private suspend fun createUser(id: String, username: String) {
        val userProfile = UserProfile(id, username, "", _imageBB.value?.url)

        storageRepository.createUser(userProfile).collect { resource ->
            _state.update { resource }
        }
        auth.logout().collect {
            if (it is Resource.Success)
                UserManager.logout()
        }
    }

    // Convert exception to resource error
    private fun handleError(e: Exception) {
        _state.update { Resource.Error(e) }
    }

}
package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.ImgBBData
import com.avcialper.lemur.data.model.UserProfile
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

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _imageBB = MutableStateFlow<ImgBBData?>(null)

    fun onUsernameChanged(username: String) {
        _username.update { username }
    }

    fun onEmailChanged(email: String) {
        _email.update { email }
    }

    fun onPasswordChanged(password: String) {
        _password.update { password }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.update { confirmPassword }
    }

    fun onImageChanged(imageUri: Uri) {
        _imageUri.update { imageUri }
    }

    // Handle signup click action
    fun onSignupClicked(convert: () -> File) = viewModelScope.launch {
        _state.update { Resource.Loading() }
        auth.signup(_email.value, _password.value).collect { resource ->
            if (resource is Resource.Success) {
                if (_imageUri.value != null) {
                    uploadImage(convert)
                    val id = resource.data?.uid!!
                    createUser(id)
                } else
                    createUser(resource.data?.uid!!)
            } else if (resource is Resource.Error)
                handleError(resource.throwable!!)
        }
    }

    // Upload image to ImgBB
    private suspend fun uploadImage(convert: () -> File) {
        val file = convert()
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                _imageBB.update { resource.data?.data }
            else if (resource is Resource.Error)
                handleError(resource.throwable!!)
        }
    }

    // Create user in Firebase storage
    private suspend fun createUser(id: String) {
        val userProfile = UserProfile(id, _username.value, _imageBB.value?.url)

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
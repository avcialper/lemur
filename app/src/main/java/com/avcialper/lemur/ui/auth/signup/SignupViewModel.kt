package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.RegisterUser
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.state.SignupState
import com.avcialper.lemur.util.constants.Resource
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

    private val _state = MutableStateFlow(SignupState())
    val state = _state.asStateFlow()

    fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username) }
    }

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onImageChanged(imageUri: Uri) {
        _state.update { it.copy(imageUri = imageUri) }
    }

    fun onSignupClicked(convert: () -> File) = viewModelScope.launch {
        val imageUri = _state.value.imageUri

        // Upload image to ImgBB
        if (imageUri != null)
            uploadImage(convert)
        else
            signup()
    }

    private fun signup() = viewModelScope.launch {
        val (username, email, password, _, _, imgBB, _) = _state.value

        auth.signup(username, email, password, imgBB).collect { resource ->
            if (resource is Resource.Success) {
                val id = resource.data?.uid!!
                val user = RegisterUser(id, username, imgBB?.url, imgBB?.deleteUrl)
                createUser(user)
            }
        }
    }

    private fun uploadImage(convert: () -> File) = viewModelScope.launch {
        val file = convert()
        _state.update { it.copy(resource = Resource.Loading()) }
        storageRepository.uploadImage(file).collect { result ->
            if (result.throwable != null) {
                _state.update { it.copy(resource = Resource.Error(result.throwable)) }
                return@collect
            } else if (result.data != null) {
                _state.update { it.copy(imgBB = result.data.data) }
                signup()
            }
        }
    }

    private fun createUser(registerUser: RegisterUser) = viewModelScope.launch {
        storageRepository.createUser(registerUser).collect { resource ->
            _state.update { it.copy(resource = resource) }
        }
    }

}
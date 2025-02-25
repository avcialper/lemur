package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.RegisterUser
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.state.SignupState
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

    private val _state = MutableStateFlow(SignupState())
    val state = _state.asStateFlow()

    fun onUsernameChanged(username: String) {
        _state.value.username = username
    }

    fun onEmailChanged(email: String) {
        _state.value.email = email
    }

    fun onPasswordChanged(password: String) {
        _state.value.password = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _state.value.confirmPassword = confirmPassword
    }

    fun onImageChanged(imageUri: Uri) {
        _state.value.imageUri = imageUri
    }

    fun onSignupClicked(convert: () -> File) = viewModelScope.launch {
        val (_, email, password, _, _, _, _) = _state.value

        auth.signup(email, password).collect { resource ->
            if (resource is Resource.Success) {
                uploadImage(convert) {
                    val id = resource.data?.uid!!
                    createUser(id)
                }
            } else if (resource.throwable != null)
                _state.update { it.copy(resource = Resource.Error(resource.throwable)) }
        }
    }

    private fun uploadImage(convert: () -> File, onSuccess: () -> Unit) = viewModelScope.launch {
        val file = convert()
        _state.update { it.copy(resource = Resource.Loading()) }
        storageRepository.uploadImage(file).collect { resource ->
            if (resource.throwable != null) {
                _state.update { it.copy(resource = Resource.Error(resource.throwable)) }
                return@collect
            } else if (resource.data != null) {
                _state.value.imgBB = resource.data.data
                onSuccess()
            }
        }
    }

    private fun createUser(id: String) = viewModelScope.launch {
        val (username, _, _, _, _, imgBB, _) = _state.value
        val registerUser = RegisterUser(id, username, imgBB?.url, imgBB?.deleteUrl)

        storageRepository.createUser(registerUser).collect { resource ->
            _state.update { it.copy(resource = resource) }
        }
    }

}
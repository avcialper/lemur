package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.UserProfile
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
        val (_, email, password, _, _, _, _) = _state.value

        auth.signup(email, password).collect { resource ->
            if (resource is Resource.Success) {
                if (_state.value.imageUri != null)
                    uploadImage(convert) {
                        val id = resource.data?.uid!!
                        createUser(id)
                    }
                else
                    createUser(resource.data?.uid!!)

            } else if (resource.throwable != null)
                _state.update { it.copy(resource = Resource.Error(resource.throwable)) }
        }
    }

    private fun uploadImage(convert: () -> File, onSuccess: () -> Unit) = viewModelScope.launch {
        val file = convert()
        _state.update { it.copy(resource = Resource.Loading()) }
        storageRepository.uploadImage(file).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            imgBB = resource.data?.data
                        )
                    }
                    onSuccess()
                }

                is Resource.Error -> _state.update { it.copy(resource = Resource.Error(resource.throwable)) }
                else -> Unit
            }
        }
    }

    private fun createUser(id: String) = viewModelScope.launch {
        val (username, _, _, _, _, imgBB, _) = _state.value
        val userProfile = UserProfile(id, username, imgBB?.url)

        storageRepository.createUser(userProfile).collect { resource ->
            _state.update { it.copy(resource = resource) }
        }
        auth.logout().collect {
            if (it is Resource.Success)
                UserManager.logout()
        }
    }

    fun clearError() {
        _state.update { it.copy(resource = null) }
    }

}
package com.avcialper.lemur.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.state.LoginState
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onLoginClicked() = viewModelScope.launch {
        val (email, password, _) = _state.value

        auth.login(email, password).collect { resource ->
            when (resource) {
                is Resource.Success -> getUser()
                is Resource.Error ->
                    _state.update { it.copy(resource = Resource.Error(resource.throwable)) }

                else -> Unit
            }
        }
    }

    private fun getUser() = viewModelScope.launch {
        val currentUser = auth.currentUser
        storageRepository.getUser(currentUser!!.uid).collect { userResource ->
            when (userResource) {
                is Resource.Success -> {
                    val (_, username, imageUrl, imageDeleteUrl) = userResource.data!!
                    UserManager.updateUser(
                        currentUser,
                        username,
                        imageUrl,
                        imageDeleteUrl
                    )
                    _state.update { it.copy(resource = Resource.Success(true)) }
                }

                is Resource.Error ->
                    _state.update { it.copy(resource = Resource.Error(userResource.throwable)) }

                else -> Unit
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(resource = null) }
    }
}

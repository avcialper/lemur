package com.avcialper.lemur.ui.profile.updateemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /*
    null -> email was not sent
    false -> email was sent
    true -> email was verified
     */
    private val _isUpdated = MutableStateFlow<Boolean?>(null)
    val isUpdated = _isUpdated.asStateFlow()

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun updateEmail() = viewModelScope.launch {
        if (AppManager.isConnected.not()) {
            _state.value = null
            return@launch
        }

        authRepository.updateEmail(email.value, password.value).collect { resource ->
            _state.value = resource
            if (resource is Resource.Success)
                _isUpdated.value = false
        }
    }

    fun login() = viewModelScope.launch {
        authRepository.login(email.value, password.value).collect { resource ->
            _isUpdated.value = resource is Resource.Success
        }
    }
}
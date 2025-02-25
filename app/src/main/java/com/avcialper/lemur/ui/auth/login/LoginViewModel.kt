package com.avcialper.lemur.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.value.email = email
    }

    fun onPasswordChanged(password: String) {
        _state.value.password = password
    }

    fun onLoginClicked() = viewModelScope.launch {
        val (email, password, _) = _state.value

        repository.login(email, password).collect { resource ->
            _state.update { it.copy(resource = resource) }
        }
    }
}
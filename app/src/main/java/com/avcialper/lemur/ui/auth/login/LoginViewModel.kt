package com.avcialper.lemur.ui.auth.login

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.repository.AuthRepository
import com.avcialper.lemur.util.constants.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val state = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onLoginClicked() {
        val email = _email.value
        val password = _password.value
        login(email, password)
    }

    private fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading()
        val result = repository.login(email, password)
        _state.value = result
    }

}
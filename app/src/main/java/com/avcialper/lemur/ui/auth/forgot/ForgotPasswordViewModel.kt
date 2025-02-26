package com.avcialper.lemur.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.state.ForgotPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun sendPasswordResetEmail() = viewModelScope.launch {
        val email = _state.value.email
        auth.forgotPassword(email).collect { resource ->
            _state.value = _state.value.copy(resource = resource)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(resource = null)
    }
}
package com.avcialper.lemur.ui.profile.updatepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val _currentPassword = MutableStateFlow("")
    val currentPassword = _currentPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateFlow()

    private val _newPasswordConfirm = MutableStateFlow("")
    val newPasswordConfirm = _newPasswordConfirm.asStateFlow()

    fun onCurrentPasswordChanged(value: String) {
        _currentPassword.value = value
    }

    fun onNewPasswordChanged(value: String) {
        _newPassword.value = value
    }

    fun onNewPasswordConfirmChanged(value: String) {
        _newPasswordConfirm.value = value
    }

    fun updatePassword() = viewModelScope.launch {
        authRepository.updatePassword(currentPassword.value, newPassword.value).collect {
            _state.value = it
        }
    }

}
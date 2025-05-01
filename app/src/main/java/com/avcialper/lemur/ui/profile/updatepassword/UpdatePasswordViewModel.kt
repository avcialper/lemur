package com.avcialper.lemur.ui.profile.updatepassword

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
class UpdatePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    fun updatePassword(currentPassword: String, newPassword: String) = viewModelScope.launch {
        if (AppManager.isConnected.not()) {
            _state.value = null
            return@launch
        }

        authRepository.updatePassword(currentPassword, newPassword).collect {
            _state.value = it
        }
    }

}
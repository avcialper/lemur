package com.avcialper.lemur.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.User
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.ResourceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            storageRepository.getUser(currentUser.uid).collect { resource ->
                if (resource.status == ResourceStatus.SUCCESS) {
                    val (_, username, imageUrl) = resource.data!!
                    UserManager.updateUser(currentUser, username, imageUrl)
                    _user.value = UserManager.user
                }
            }
        }
    }

    fun logout(onCompleted: () -> Unit) = viewModelScope.launch {
        auth.logout().collect { resource ->
            if (resource.status == ResourceStatus.SUCCESS) {
                UserManager.logout()
                onCompleted()
            }
        }
    }

    fun sendEmailVerification(onCompleted: () -> Unit) = viewModelScope.launch {
        auth.sendEmailVerification().collect { resource ->
            if (resource.status == ResourceStatus.SUCCESS) {
                onCompleted.invoke()
            }
        }
    }

    fun reloadData() = viewModelScope.launch {
        getUser()
    }

}
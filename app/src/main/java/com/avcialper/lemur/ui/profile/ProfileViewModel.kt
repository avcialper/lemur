package com.avcialper.lemur.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.user.UserRepository
import com.avcialper.lemur.helper.DataStoreManager
import com.avcialper.lemur.util.constant.ResourceStatus
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _user = MutableStateFlow(UserManager.user)
    val user = _user.asStateFlow()

    val theme = dataStoreManager.theme
    val notificationPermission = dataStoreManager.notificationPermission

    init {
        reloadData()
    }

    fun reloadData() = viewModelScope.launch {
        auth.currentUser?.let {
            auth.reload().collect { resignedUser ->
                resignedUser?.let { getUserFromRepository(it) }
            }
        }
    }

    private suspend fun getUserFromRepository(user: FirebaseUser) {
        userRepository.getUser(user.uid).collect { resource ->
            if (resource.status == ResourceStatus.SUCCESS) {
                resource.data?.let { (_, username, about, imageUrl, teams) ->
                    UserManager.updateUser(user, username, about, imageUrl, teams ?: emptyList())
                    _user.update { UserManager.user }
                }
            }
        }
    }

    fun logout(onCompleted: () -> Unit) = viewModelScope.launch {
        auth.logout().collect { resource ->
            if (resource.status == ResourceStatus.SUCCESS) {
                UserManager.logout()
                _user.value = null
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

    fun changeNotificationPermission() = viewModelScope.launch {
        dataStoreManager.changeNotificationPermission()
    }
}
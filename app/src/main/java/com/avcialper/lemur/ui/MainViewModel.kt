package com.avcialper.lemur.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.helper.ConnectivityObserver
import com.avcialper.lemur.helper.DataStoreManager
import com.avcialper.lemur.util.constant.ResourceStatus
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository,
    private val dataStoreManager: DataStoreManager,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()

    private val _isCurrentUserChecked = MutableStateFlow(false)
    val isCurrentUserChecked = _isCurrentUserChecked.asStateFlow()

    private val _isThemeChecked = MutableStateFlow(false)
    val isThemeChecked = _isThemeChecked.asStateFlow()

    val isConnected = connectivityObserver.isConnected

    init {
        loadTheme()
        getUser()
    }

    private fun loadTheme() = viewModelScope.launch {
        dataStoreManager.loadTheme()
        _isThemeChecked.update { true }
    }

    private fun getUser() {
        val user = auth.currentUser
        if (user != null)
            reload()
        else
            _isCurrentUserChecked.update { true }
    }

    private fun reload() = viewModelScope.launch {
        auth.reload().collect { reloadedUser ->
            if (reloadedUser != null)
                getUserFromRepository(reloadedUser)
            else
                _isCurrentUserChecked.update { true }
        }
    }

    private suspend fun getUserFromRepository(user: FirebaseUser) {
        val resource = storageRepository.getUser(user.uid).firstOrNull()
        if (resource?.status == ResourceStatus.SUCCESS) {
            resource.data?.let { (_, username, about, imageUrl) ->
                UserManager.updateUser(user, username, about, imageUrl)
                _user.update { user }
            }
        }
        _isCurrentUserChecked.update { true }
    }

}
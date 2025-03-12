package com.avcialper.lemur.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.state.MainState
import com.avcialper.lemur.helper.ThemeManager
import com.avcialper.lemur.util.constant.ResourceStatus
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository,
    private val themeManager: ThemeManager,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        loadTheme()
        getUser()
    }

    private fun loadTheme() = viewModelScope.launch {
        themeManager.loadTheme()
        _state.update { it.copy(isThemeChecked = true) }
    }

    private fun getUser() {
        val user = auth.currentUser
        if (user != null)
            reload()
        else
            _state.update { it.copy(isCurrentUserChecked = true) }
    }

    private fun reload() = viewModelScope.launch {
        auth.reload().collect { resignedUser ->
            if (resignedUser != null)
                getUserFromRepository(resignedUser)
            else
                _state.update { it.copy(isCurrentUserChecked = true) }
        }
    }

    private suspend fun getUserFromRepository(user: FirebaseUser) {
        storageRepository.getUser(user.uid).collect { resource ->
            if (resource.status == ResourceStatus.SUCCESS) {
                resource.data?.let { (_, username, imageUrl) ->
                    UserManager.updateUser(user, username, imageUrl)
                    _state.update { it.copy(user = user, isCurrentUserChecked = true) }
                }
            }
        }
    }

}
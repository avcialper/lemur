package com.avcialper.lemur.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted = _isCompleted.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            storageRepository.getUser(currentUser.uid).collect { resource ->
                if (resource is Resource.Success) {
                    val (_, username, imageUrl, imageDeleteUrl) = resource.data!!
                    UserManager.updateUser(currentUser, username, imageUrl, imageDeleteUrl)
                }
            }
        }
        _isCompleted.value = true
    }
}
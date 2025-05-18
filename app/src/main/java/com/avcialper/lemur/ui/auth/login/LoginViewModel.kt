package com.avcialper.lemur.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val state = _state.asStateFlow()

    // Handle login click action
    fun onLoginClicked(email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading()

        if (AppManager.isConnected.not()) {
            _state.value = null
            return@launch
        }

        auth.login(email, password).collect { resource ->
            if (resource is Resource.Success)
                getUser()
            else if (resource is Resource.Error)
                _state.value = Resource.Error(resource.throwable)

        }
    }

    // If user is logged in successfully get user data from storage
    private suspend fun getUser() {
        val currentUser = auth.currentUser
        storageRepository.getUser(currentUser!!.uid).collect { resource ->
            when (resource) {
                is Resource.Error -> _state.update { Resource.Error(resource.throwable) }
                is Resource.Loading -> _state.update { Resource.Loading() }
                is Resource.Success -> {
                    val (_, username, about, imageUrl, teams) = resource.data!!
                    UserManager.updateUser(
                        currentUser,
                        username,
                        about,
                        imageUrl,
                        teams ?: emptyList()
                    )
                    _state.update { Resource.Success(currentUser) }
                }
            }
        }
    }
}

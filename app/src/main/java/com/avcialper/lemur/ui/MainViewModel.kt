package com.avcialper.lemur.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.helper.ConnectivityObserver
import com.avcialper.lemur.helper.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: AuthRepository,
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
        val firebaseUser = auth.currentUser
        if (firebaseUser != null)
            UserManager.updateUser(firebaseUser)
        _isCurrentUserChecked.update { true }
    }

}
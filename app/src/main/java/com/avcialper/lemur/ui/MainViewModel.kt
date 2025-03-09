package com.avcialper.lemur.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.helper.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _isCompeted = MutableStateFlow(false)
    val isCompeted = _isCompeted.asStateFlow()

    init {
        loadTheme()
    }

    private fun loadTheme() = viewModelScope.launch {
        themeManager.loadTheme()
        _isCompeted.value = true
    }

}
package com.avcialper.lemur.ui.component.themeselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.helper.DataStoreManager
import com.avcialper.lemur.util.constant.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSelectorViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val theme = dataStoreManager.theme

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        dataStoreManager.changeTheme(theme)
    }

}
package com.avcialper.lemur.ui.component.themeselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.helper.ThemeManager
import com.avcialper.lemur.util.constant.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSelectorViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    val theme = themeManager.theme

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        themeManager.changeTheme(theme)
    }

}
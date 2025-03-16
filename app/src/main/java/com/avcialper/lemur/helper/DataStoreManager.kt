package com.avcialper.lemur.helper

import androidx.appcompat.app.AppCompatDelegate
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.repository.datastore.DataStoreRepository
import com.avcialper.lemur.util.constant.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {

    val theme = dataStoreRepository.getTheme()
    val notificationPermission = dataStoreRepository.getNotificationPermission()

    suspend fun loadTheme() {
        val theme = dataStoreRepository.getTheme().first()
        applyTheme(theme)
        AppManager.theme = theme
        delay(100) // wait theme update
    }

    suspend fun changeTheme(theme: Theme) {
        dataStoreRepository.setTheme(theme)
        AppManager.theme = theme
        applyTheme(theme)
    }

    private fun applyTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM_DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    suspend fun changeNotificationPermission() {
        val isGranted = AppManager.notificationPermission.not()
        dataStoreRepository.setNotificationPermission(isGranted)
    }
}

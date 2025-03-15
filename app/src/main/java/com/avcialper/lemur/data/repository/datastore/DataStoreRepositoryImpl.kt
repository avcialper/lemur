package com.avcialper.lemur.data.repository.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avcialper.lemur.util.constant.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    private val datastore: DataStore<Preferences>
) : DataStoreRepository {

    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val NOTIFICATION_PERMISSION_KEY = booleanPreferencesKey("notification_permission")
    }

    override fun getTheme(): Flow<Theme> {
        return datastore.data.map { preferences ->
            val theme = preferences[THEME_KEY]
            Theme.fromString(theme)
        }
    }

    override suspend fun setTheme(theme: Theme) {
        datastore.edit { preferences ->
            preferences[THEME_KEY] = theme.value
        }
    }

    override fun getNotificationPermission(): Flow<Boolean> {
        return datastore.data.map { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] ?: false
        }
    }

    override suspend fun setNotificationPermission(isGranted: Boolean) {
        datastore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] = isGranted
        }
    }
}
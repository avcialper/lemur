package com.avcialper.lemur.data.repository.datastore

import com.avcialper.lemur.util.constant.Theme
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    fun getTheme(): Flow<Theme>
    suspend fun setTheme(theme: Theme)
    fun getNotificationPermission(): Flow<Boolean>
    suspend fun setNotificationPermission(isGranted: Boolean)
}
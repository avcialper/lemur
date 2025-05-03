package com.avcialper.lemur.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.auth.AuthRepositoryImpl
import com.avcialper.lemur.data.repository.datastore.DataStoreRepository
import com.avcialper.lemur.data.repository.datastore.DataStoreRepositoryImpl
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.repository.storage.StorageRepositoryImpl
import com.avcialper.lemur.util.constant.Constants.DATASTORE_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideStorageRepository(api: StorageApi, firestore: FirebaseFirestore): StorageRepository =
        StorageRepositoryImpl(api, firestore)

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository =
        DataStoreRepositoryImpl(context.dataStore)
}
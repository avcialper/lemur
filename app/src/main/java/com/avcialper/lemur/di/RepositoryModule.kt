package com.avcialper.lemur.di

import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.auth.AuthRepositoryImpl
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.repository.storage.StorageRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideStorageRepository(api: StorageApi, firestore: FirebaseFirestore): StorageRepository =
        StorageRepositoryImpl(api, firestore)
}
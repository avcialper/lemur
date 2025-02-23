package com.avcialper.lemur.di

import com.avcialper.lemur.data.repository.AuthRepository
import com.avcialper.lemur.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
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

}